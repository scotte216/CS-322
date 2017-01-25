	.text
			  # _main () (x)
	.p2align 4,0x90
	.globl _main
_main:
	subq $24,%rsp
			  #  t1 = 1 + 2
	movq $1,%r10
	movq $2,%r11
	addq %r11,%r10
	movl %r10d,4(%rsp)
			  #  t2 = 1 - 1
	movq $1,%r10
	movq $1,%r11
	subq %r11,%r10
	movl %r10d,8(%rsp)
			  #  t3 = t1 > t2
	movslq 4(%rsp),%r10
	movslq 8(%rsp),%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %r10d,12(%rsp)
			  #  if t3 == false goto L0
	movslq 12(%rsp),%r10
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
	addq  $24,%rsp
	ret
			  # Total inst cnt: 28
