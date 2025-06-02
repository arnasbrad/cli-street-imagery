# Pavyzdinis Mapillary raktas

```
MLY|1111111111111111|22222222222222222233333333333333
```

# Adresas

## Kauno Pilis - 1

```
java -jar street-ascii.jar address "Kauno pilis" --config ./sequencenav.conf
```

# Koordinatės

## Soboras - 2

```
java -jar street-ascii.jar coordinates "54.8968855,23.9202792" --radius 1
```

## Pažaislio kiemelis - 3

```
java -jar street-ascii.jar coordinates "54.876282420000024,24.019055750000007" --radius 1
```

## Pažaislis iš šono - 4

```
java -jar street-ascii.jar coordinates "54.875859750008,24.020683770015" --radius 1
```

# ID

## Eifelio bokštas - 5

```
java -jar street-ascii.jar id 1597574490633156 --config ./radiusnav.conf
```

## KTU takelis - 6

```
java -jar street-ascii.jar id 1360372998442846 --config ./extendedchar.conf
```

## KTU studentų g. - 7

```
java -jar street-ascii.jar id 1397507581259686
```

## KTU XI rūmai - 8

```
java -jar street-ascii.jar id 953670603327414 --config ./noalgorithm.conf
```

## KTU žaibas - 9

```
java -jar street-ascii.jar id 2011364102721524 --config ./extendedchar.conf; \
java -jar street-ascii.jar id 2011364102721524 --config ./edgedetectionsobel.conf
```

# Spėliojimas - 10

```
java -jar street-ascii.jar guessing
```

