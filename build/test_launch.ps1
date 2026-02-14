Set-Location $PSScriptRoot
$p = Start-Process .\uqm.exe -PassThru
Start-Sleep 8
if ($p.HasExited) {
    "CRASHED: exit code $($p.ExitCode)" | Out-File test_result.txt
} else {
    "ALIVE: PID $($p.Id)" | Out-File test_result.txt
    Stop-Process $p.Id -Force
}
