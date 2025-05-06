# Adresas

## Kauno Pilis
java -jar street-ascii.jar address "Kauno pilis"

# Koordinatės

## Soboras
java -jar street-ascii.jar coordinates "54.8968855,23.9202792" --radius 1

## Pažaislio kiemelis
java -jar street-ascii.jar coordinates "54.876282420000024,24.019055750000007" --radius 1

## Pažaislis iš šono
java -jar street-ascii.jar coordinates "54.875859750008,24.020683770015" --radius 1

# ID

## Eifelio bokštas
java -jar street-ascii.jar id 1597574490633156 --config ./radiusnav.conf

## KTU takelis
java -jar street-ascii.jar id 1360372998442846 --config ./extendedchar.conf

## KTU studentų g.
java -jar street-ascii.jar id 1397507581259686

## KTU XI rūmai
java -jar street-ascii.jar id 953670603327414 --config ./noalgorithm.conf

## KTU žaibas
java -jar street-ascii.jar id 2011364102721524 --config ./extendedchar.conf; \
java -jar street-ascii.jar id 2011364102721524 --config ./edgedetectionsobel.conf

# Spėliojimas

java -jar street-ascii.jar guessing

