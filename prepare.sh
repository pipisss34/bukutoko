#!/data/data/com.termux/files/usr/bin/bash
# Salin icon, splash, dan index.html terbaru dari folder Download ke proyek.
set -e
D="$HOME/storage/downloads"
R="app/src/main/res"
[ -d "$D" ] || { echo "Jalankan dulu: termux-setup-storage"; exit 1; }
pick(){ find "$D/" -maxdepth 1 -type f \( -iname "*$1*.png" -o -iname "*$1*.webp" -o -iname "*$1*.jpg" -o -iname "*$1*.jpeg" \) -printf '%T@ %p\n' | sort -rn | head -1 | cut -d' ' -f2-; }
ICON=$(pick icon); SPL=$(pick splash)
[ -n "$ICON" ] || { echo "File icon tidak ketemu di Download (nama harus mengandung 'icon')"; exit 1; }
[ -n "$SPL" ]  || { echo "File splash tidak ketemu di Download (nama harus mengandung 'splash')"; exit 1; }
ext(){ e="${1##*.}"; echo "${e,,}"; }
mkdir -p $R/mipmap-xxxhdpi $R/drawable-nodpi
rm -f $R/mipmap-xxxhdpi/ic_launcher.* $R/drawable-nodpi/splash.*
cp "$ICON" "$R/mipmap-xxxhdpi/ic_launcher.$(ext "$ICON")"
cp "$SPL"  "$R/drawable-nodpi/splash.$(ext "$SPL")"
[ -f "$D/index.html" ] && cp "$D/index.html" app/src/main/assets/index.html
echo "OK icon  : $ICON"
echo "OK splash: $SPL"
echo "OK html  : $( [ -f "$D/index.html" ] && echo "$D/index.html" || echo bawaan proyek )"
