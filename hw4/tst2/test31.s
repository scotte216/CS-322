	.text
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
	subq $52,%rsp
			  #  t1 = call _malloc(12)
	movq $12,%rdi
	call _malloc
	movl %eax,(%rsp)
			  #  a = t1
	movslq (%rsp),%r10
	movl %r10d,4(%rsp)
			  #  t2 = 0 * 4
	movq $0,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,8(%rsp)
			  #  t3 = a + t2
	movslq 4(%rsp),%r10
	movslq 8(%rsp),%r11
	addq %r11,%r10
	movl %r10d,12(%rsp)
			  #  [t3] = 3
	movq $3,%r10
	movslq 12(%rsp),%r11
	movl %r10d,(%r11)
			  #  t4 = 1 * 4
	movq $1,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,16(%rsp)
			  #  t5 = a + t4
	movslq 4(%rsp),%r10
	movslq 16(%rsp),%r11
	addq %r11,%r10
	movl %r10d,20(%rsp)
			  #  [t5] = 4
	movq $4,%r10
	movslq 20(%rsp),%r11
	movl %r10d,(%r11)
			  #  t6 = 2 * 4
	movq $2,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,24(%rsp)
			  #  t7 = a + t6
	movslq 4(%rsp),%r10
	movslq 24(%rsp),%r11
	addq %r11,%r10
	movl %r10d,28(%rsp)
			  #  [t7] = 5
	movq $5,%r10
	movslq 28(%rsp),%r11
	movl %r10d,(%r11)
			  #  t8 = 2 * 4
	movq $2,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,32(%rsp)
			  #  t9 = a + t8
	movslq 4(%rsp),%r10
	movslq 32(%rsp),%r11
	addq %r11,%r10
	movl %r10d,36(%rsp)
			  #  t10 = [t9]
	movslq 36(%rsp),%r10
	movslq (%r10),%r11
	movl %r11d,40(%rsp)
			  #  call _printInt(t10)
	movslq 40(%rsp),%rdi
	call _printInt
			  #  return 
	addq  $52,%rsp
	ret
			  # Total inst cnt: 57
