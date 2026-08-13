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
git push -u origin $Branch

$template = Get-Content ".github/PULL_REQUEST_TEMPLATE.md" -Raw
$body = $template.Replace("{title}", $Title).Replace("{description}", $Description)

& $gh pr create --title $Title --body $body --base main
