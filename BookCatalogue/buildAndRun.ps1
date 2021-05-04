function ToArray {
    begin {
        $out = @()
    }

    process {
        $out += $_
    }

    end {
        return $out
    }
}

cmd.exe /c build.bat # build the project with maven

$cp = Get-Content backend/cp.txt
$cpArray = $cp.Split(";")
$cpFixedArray = $cpArray | % {$_.substring(0, $_.LastIndexOf("\") + 1)} | ToArray
$cpFixedArray += "backend\target"
#write-output "Fixed array: "
#write-output $cpFixedArray
$cpGui = Get-Content gui/cp.txt
$cpGuiArray = $cpGui.Split(";")
$cpGuiFixedArray = $cpGuiArray | % {$_.substring(0, $_.LastIndexOf("\") + 1)} | ToArray
$cpGuiFixedArray += "gui\target"

$cpGenre = Get-Content genre/cp.txt
$cpGenreArray = $cpGenre.Split(";")
$cpGenreFixedArray = $cpGenreArray | % {$_.substring(0, $_.LastIndexOf("\") + 1)} | ToArray
$cpGenreFixedArray += "genre\target"

$finalArray = $cpFixedArray + $cpGuiFixedArray# + $cpGenreFixedArray

$modulePath = ($finalArray -join ';')
#& 'C:\Program Files\Java\jdk-9.0.4\bin\java' -p "$modulePath" -cp "$cp" -m backend/com.fostecar000.backend.Test
#& 'C:\Program Files\Java\jdk-11.0.10\bin\java' -p "$modulePath" -cp "$cp" -m backend/com.fostecar000.backend.Test -ea
& 'C:\Program Files\Java\jdk-11.0.10\bin\java' -p "$modulePath" -cp "$cp" -m gui/com.fostecar000.gui.Test -ea
#& 'C:\Program Files\Java\jdk-11.0.10\bin\java' -Xmx8g -cp ($cpGenre + ";genre\target\classes") com.fostecar000.genre.Test -ea