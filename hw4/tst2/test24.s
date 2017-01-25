	.text
			  # _main () (x)
	.p2align 4,0x90
	.globl _main
_main:
	subq $8,%rsp
			  #  t1 = 3 > 0
	movq $3,%r10
	movq $0,%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %r10d,4(%rsp)
			  #  if t1 == false goto L0
	movslq 4(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L0
			  #  x = 1
	movq $1,%r10
	movl %r10d,(%rsp)
			  # L0:
_main_L0:
			  #  call _printInt(x)
	movslq (%rsp),%rdi
	call _printInt
			  #  return 
	addq  $8,%rsp
	ret
			  # Total inst cnt: 20
