param(
    [string]$Title,
    [string]$Description,
    [string]$Branch
)

$gh = "C:\Program Files\GitHub CLI\gh.exe"

if (-not $Title) {
    $Title = Read-Host "Título del PR"
    $Title = $Title.Trim()
}
if (-not $Title) {
    Write-Host "El título es obligatorio." -ForegroundColor Red
    exit 1
}

if (-not $Description) {
    $Description = Read-Host "Descripción de la PR"
    $Description = $Description.Trim()
}
if (-not $Description) {
    $Description = "Sin descripción."
}

if (-not $Branch) {
    $Branch = ($Title.ToLower() -replace '[^a-z0-9]+', '-' -replace '^-+|-+$', '')
}

git checkout -b $Branch 2>$null
if (-not $?) {
    git checkout $Branch
}

git add -A
git commit -m $Title

# Comprobar si la rama está actualizada con main
git fetch origin main
$behind = git rev-list --count "HEAD..origin/main" 2>$null
if ($behind -and [int]$behind -gt 0) {
    Write-Host "La rama está $behind commit(s) por detrás de main. Intentando merge..." -ForegroundColor Yellow
    git merge origin/main
    if (-not $?) {
        Write-Host "Conflicto al integrar main. Resuelve los conflictos o cierra el merge." -ForegroundColor Red
        git merge --abort
        exit 1
    }
    Write-Host "Merge con main completado." -ForegroundColor Green
}

git push -u origin $Branch

$template = Get-Content ".github/PULL_REQUEST_TEMPLATE.md" -Raw
$body = $template.Replace("{title}", $Title).Replace("{description}", $Description)

& $gh pr create --title $Title --body $body --base main
