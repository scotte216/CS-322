	.text
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
	subq $8,%rsp
			  #  x = -1
	movq $1,%r10
	negq %r10
	movl %r10d,(%rsp)
			  #  call _printInt(x)
	movslq (%rsp),%rdi
	call _printInt
			  #  call _printStr()
	leaq _S0(%rip),%rdi
	call _printStr
			  #  return 
	addq  $8,%rsp
	ret
_S0:
	.asciz ""
			  # Total inst cnt: 13
