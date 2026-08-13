param(
    [Parameter(Mandatory = $true)][string]$Title,
    [string]$Branch
)

$gh = "C:\Program Files\GitHub CLI\gh.exe"

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
& $gh pr create --title $Title --body $template --base main
