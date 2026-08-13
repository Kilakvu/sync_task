param(
    [Parameter(Mandatory = $true)][string]$Version
)

# Script de promoción a producción: crea un tag vX.Y.Z sobre develop
# y lo sube a GitHub. El push del tag dispara el workflow deploy-prod.

# Validar formato semver
if ($Version -notmatch '^\d+\.\d+\.\d+$') {
    Write-Host "Versión inválida. Usa formato semver: 1.2.3" -ForegroundColor Red
    exit 1
}

$Tag = "v$Version"

git checkout develop 2>$null
if (-not $?) {
    Write-Host "No existe la rama develop local." -ForegroundColor Red
    exit 1
}

git pull origin develop
if (-not $?) {
    Write-Host "No se pudo actualizar develop desde origin." -ForegroundColor Red
    exit 1
}

if (git tag -l $Tag) {
    Write-Host "El tag $Tag ya existe." -ForegroundColor Red
    exit 1
}

$Message = Read-Host "Mensaje del tag (deja vacío para usar solo la versión)"
if (-not $Message) { $Message = "Release $Tag" }

git tag -a $Tag -m $Message
if (-not $?) {
    Write-Host "No se pudo crear el tag." -ForegroundColor Red
    exit 1
}

git push origin $Tag
if (-not $?) {
    Write-Host "No se pudo subir el tag." -ForegroundColor Red
    git tag -d $Tag
    exit 1
}

Write-Host "Tag $Tag creado y subido. El workflow deploy-prod ya está en marcha." -ForegroundColor Green
Write-Host "Sigue el despliegue en: https://github.com/Kilakvu/sync_task/actions"
