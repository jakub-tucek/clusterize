args = commandArgs(trailingOnly=TRUE)


# test if there is at least one argument: if not, return an error
if (length(args)==0) {
  stop("At least one argument must be supplied (input file).n", call.=FALSE)
}



print(args[2])

## program...
df = read.table(args[1], header=TRUE)
num_vars = which(sapply(df, class)=="numeric")
df_out = df[ ,num_vars]
write.table(df_out, file=args[3], row.names=FALSE)
