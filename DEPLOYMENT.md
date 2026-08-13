# Guía de Despliegue (Preproducción y Producción)

Este documento explica cómo desplegar la aplicación en los entornos **preprod** y **prod** usando GitHub Actions y GitHub Environments.

## 1. Modelo de Entornos

| Entorno  | Rama de despliegue | Cuándo se ejecuta        | Requiere aprobación |
|----------|--------------------|--------------------------|---------------------|
| preprod  | `develop`          | push a `develop`         | No                  |
| prod     | `main`             | merge a `main` (vía PR)  | Opcional (ver §3)   |

- La rama **`develop`** es el entorno de integración/pruebas.
- La rama **`main`** es producción y está **protegida** (solo se actualiza mediante PR con CI en verde).

## 2. Configuración ya realizada en GitHub

- Entornos creados: `preprod` y `prod` (Settings → Environments).
- `prod` restringido: solo se puede desplegar desde `main` (deployment branch policy).
- Secreto `API_KEY` en cada entorno (la misma clave que exige el header `X-API-Key`).

### Comprobar secretos por entorno

```bash
gh secret list -e preprod
gh secret list -e prod
```

Cambiar una clave de API:

```bash
gh secret set API_KEY -e preprod --body "nueva-clave-preprod"
gh secret set API_KEY -e prod --body "nueva-clave-prod"
```

> ⚠️ La clave del entorno debe coincidir con la env var `API_KEY` que usa la app desplegada. Si no coincide, las llamadas devolverán `401`.

## 3. Configurar aprobación manual en Producción (recomendado)

Settings → Environments → `prod` → **Required reviewers** → añade tu usuario.

Con esto, cuando el workflow de producción llegue al job de despliegue, GitHub **pausará** la ejecución hasta que apruebes manualmente la acción. En un proyecto en solitario puedes auto-aprobarte.

## 4. Secretos y variables necesarios para un despliegue real

El workflow de ejemplo solo imprime los valores. Para un despliegue real (p. ej. VPS con Docker), añade por entorno:

| Tipo    | Nombre       | Ejemplo preprod            | Ejemplo prod                |
|---------|--------------|----------------------------|-----------------------------|
| Var     | `API_URL`    | `https://preprod.midominio.com` | `https://api.midominio.com` |
| Var     | `DEPLOY_HOST`| `ssh.preprod.midominio.com`| `ssh.api.midominio.com`     |
| Secret  | `SSH_KEY`    | clave privada SSH (PEM)    | clave privada SSH (PEM)     |
| Secret  | `DB_PASSWORD`| `...`                      | `...`                       |

Variables:

```bash
gh variable set API_URL -e preprod --body "https://preprod.midominio.com"
gh variable set DEPLOY_HOST -e preprod --body "ssh.preprod.midominio.com"
```

Secretos:

```bash
gh secret set SSH_KEY -e preprod --body "contenido de la clave PEM"
gh secret set DB_PASSWORD -e preprod --body "password-preprod"
gh secret set API_KEY -e preprod --body "preprod-api-key"
```

## 5. Adaptar el workflow de despliegue

`.github/workflows/deploy.yml` tiene el esqueleto. Ejemplo real para un VPS con Docker Compose vía SSH:

```yaml
name: Continuous Deployment

on:
  push:
    branches: [main, develop]

permissions:
  contents: read

jobs:
  deploy-preprod:
    if: github.ref == 'refs/heads/develop'
    runs-on: ubuntu-latest
    environment: preprod
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Desplegar en preproducción
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ vars.DEPLOY_HOST }}
          username: deploy
          key: ${{ secrets.SSH_KEY }}
          script: |
            cd /opt/sync_task
            echo "API_KEY=${{ secrets.API_KEY }}" > .env
            echo "DB_PASSWORD=${{ secrets.DB_PASSWORD }}" >> .env
            git pull origin develop
            docker compose up -d --build

  deploy-prod:
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    environment: prod
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Desplegar en producción
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ vars.DEPLOY_HOST }}
          username: deploy
          key: ${{ secrets.SSH_KEY }}
          script: |
            cd /opt/sync_task
            echo "API_KEY=${{ secrets.API_KEY }}" > .env
            echo "DB_PASSWORD=${{ secrets.DB_PASSWORD }}" >> .env
            git pull origin main
            docker compose up -d --build
```

## 6. Proceso de despliegue paso a paso

### Preproducción (desde `develop`)

1. Haz tus cambios en una rama `feat/...`.
2. Crea la PR hacia `develop` (usa `pr "título"`).
3. Mergea la PR → el push a `develop` dispara el job `deploy-preprod`.
4. Verifica en la pestaña **Actions** que el despliegue acaba en verde.
5. Prueba contra la URL de preprod (clave: `API_KEY` de ese entorno).

### Producción (desde `main`)

1. Cuando `develop` esté estable, crea una PR de `develop` → `main`.
2. El CI (`Build y test`) debe pasar antes de mergear.
3. Mergea → se dispara `deploy-prod`.
4. Si hay `Required reviewers` en `prod`, aprueba la ejecución desde la pestaña **Actions** → el job en espera.
5. Verifica la URL de producción.

## 7. Verificación tras desplegar

```bash
# Healthcheck básico
curl -f -H "X-API-Key: $CLAVE_DEL_ENTORNO" https://api.midominio.com/api/v1/tasks

# Swagger UI (accesible sin clave)
curl -f https://api.midominio.com/swagger-ui.html
```

También puedes usar Bruno: selecciona el entorno `Preprod` o `Prod` en la esquina superior derecha; las variables `{{baseUrl}}` y `{{apiKey}}` se actualizan solas.

## 8. Rollback

GitHub Actions no gestiona rollback de forma nativa. Opciones:

- **Revert del merge**: `git revert <sha>` en `develop`/`main` vía PR → el workflow redeshiega la versión anterior.
- **Tag previo de la imagen**: si la imagen Docker se versiona (`:vX.Y.Z`), despliega el tag anterior con `docker compose up -d --build` apuntando a ese tag.

## 9. Notas importantes

- `main` está protegido: no se puede hacer push directo, todo entra por PR con CI verde.
- El healthcheck de Docker envía `X-API-Key: dev-key-123`; si cambias `API_KEY` en el entorno, actualiza también esa línea en `docker-compose.yml`.
- Cada entorno usa la misma imagen y el mismo código, solo cambian las **variables y secretos** (API key, BD, dominio).
