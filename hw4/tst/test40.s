	.text
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
	subq $8,%rsp
			  #  call _printInt(123)
	movq $123,%rdi
	call _printInt
			  #  call _printStr("abc")
	leaq _S0(%rip),%rdi
	call _printStr
			  #  call _printStr("second string")
	leaq _S1(%rip),%rdi
	call _printStr
			  #  call _printBool(true)
	movq $1,%rdi
	call _printBool
			  #  call _printStr()
	leaq _S2(%rip),%rdi
	call _printStr
			  #  return 
	addq  $8,%rsp
	ret
_S0:
	.asciz "abc"
_S1:
	.asciz "second string"
_S2:
	.asciz ""
			  # Total inst cnt: 16
