# SmartFactory Execution Script for PowerShell
Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host " Compiling SmartFactory Industrial Analytics Platform (Java 17+)..." -ForegroundColor Cyan
Write-Host "================================================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

javac -d bin -sourcepath src src/smartfactory/Main.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed. Ensure JDK 17+ is installed and on PATH." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[OK] Compilation successful.`n" -ForegroundColor Green
Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host " Running SmartFactory Main Demonstration Suite..." -ForegroundColor Cyan
Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host ""

java -cp bin smartfactory.Main
