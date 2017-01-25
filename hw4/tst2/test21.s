	.text
			  # _main () (x, y)
	.p2align 4,0x90
	.globl _main
_main:
	subq $28,%rsp
			  #  t1 = 2 * 4
	movq $2,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,8(%rsp)
			  #  t2 = 1 + t1
	movq $1,%r10
	movslq 8(%rsp),%r11
	addq %r11,%r10
	movl %r10d,12(%rsp)
			  #  x = t2
	movslq 12(%rsp),%r10
	movl %r10d,(%rsp)
			  #  t3 = -5
	movq $5,%r10
	negq %r10
	movl %r10d,16(%rsp)
			  #  t4 = t3 + 2
	movslq 16(%rsp),%r10
	movq $2,%r11
	addq %r11,%r10
	movl %r10d,20(%rsp)
			  #  y = t4
	movslq 20(%rsp),%r10
	movl %r10d,4(%rsp)
			  #  t5 = x - y
	movslq (%rsp),%r10
	movslq 4(%rsp),%r11
	subq %r11,%r10
	movl %r10d,24(%rsp)
			  #  call _printInt(t5)
	movslq 24(%rsp),%rdi
	call _printInt
			  #  return 
	addq  $28,%rsp
	ret
			  # Total inst cnt: 31
