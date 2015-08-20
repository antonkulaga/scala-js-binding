
import org.denigma.binding.macroses.CSV
val where = "/home/antonkulaga/Downloads/state_table.csv"
/*
val mp = CSV.toVectorMap("/home/antonkulaga/Downloads/state_table.csv")
val head = mp.head
val m = head.toMap*/

val mp = CSV.toDataFrame("/home/antonkulaga/Downloads/state_table.csv")
val rows = mp.rows
val cols = mp.cols
mp.headers
mp.name.zip(mp.abbreviation)
