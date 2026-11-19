$files = @(
    "SMARTCART/app/src/main/java/com/simats/smartcart/ai/SmartCartAiEngine.kt",
    "SMARTCART/app/src/main/java/com/simats/smartcart/data/StoreComparisonRepository.kt",
    "SMARTCART/app/src/main/java/com/simats/smartcart/ui/screens/AmazonFlipkartCompareCard.kt",
    "SMARTCART/app/src/main/java/com/simats/smartcart/ui/screens/FullProductOverviewScreen.kt",
    "SMARTCART/app/src/main/java/com/simats/smartcart/ui/screens/ProductDetailScreen.kt"
)

foreach ($f in $files) {
    $c = [System.IO.File]::ReadAllText($f)
    $open = ([System.Text.RegularExpressions.Regex]::Matches($c, '\{')).Count
    $close = ([System.Text.RegularExpressions.Regex]::Matches($c, '\}')).Count
    Write-Host "$f => Open: $open, Close: $close, Matches: $($open -eq $close)"
}

