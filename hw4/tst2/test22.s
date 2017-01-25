	.text
			  # _main () (x, y)
	.p2align 4,0x90
	.globl _main
_main:
	subq $36,%rsp
			  #  t2 = true
	movq $1,%r10
	movl %r10d,8(%rsp)
			  #  t1 = 1 > 0
	movq $1,%r10
	movq $0,%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %r10d,12(%rsp)
			  #  if t1 == true goto L0
	movslq 12(%rsp),%r10
	movq $1,%r11
	cmpq %r11,%r10
	je _main_L0
			  #  t3 = 1 > 2
	movq $1,%r10
	movq $2,%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %r10d,16(%rsp)
			  #  t2 = t3
	movslq 16(%rsp),%r10
	movl %r10d,8(%rsp)
			  # L0:
_main_L0:
			  #  x = t2
	movslq 8(%rsp),%r10
	movl %r10d,(%rsp)
			  #  t4 = 1 + 2
	movq $1,%r10
	movq $2,%r11
	addq %r11,%r10
	movl %r10d,20(%rsp)
			  #  t5 = 3 - 1
	movq $3,%r10
	movq $1,%r11
	subq %r11,%r10
	movl %r10d,24(%rsp)
			  #  t6 = t4 * t5
	movslq 20(%rsp),%r10
	movslq 24(%rsp),%r11
	imulq %r11,%r10
	movl %r10d,28(%rsp)
			  #  y = t6
	movslq 28(%rsp),%r10
	movl %r10d,4(%rsp)
			  #  call _printBool(x)
	movslq (%rsp),%rdi
	call _printBool
			  #  call _printInt(y)
	movslq 4(%rsp),%rdi
	call _printInt
			  #  return 
	addq  $36,%rsp
	ret
			  # Total inst cnt: 46
