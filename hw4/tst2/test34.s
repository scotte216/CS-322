	.text
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
	subq $76,%rsp
			  #  t1 = call _malloc(8)
	movq $8,%rdi
	call _malloc
	movl %eax,(%rsp)
			  #  a = t1
	movslq (%rsp),%r10
	movl %r10d,4(%rsp)
			  #  flag = true
	movq $1,%r10
	movl %r10d,8(%rsp)
			  #  t2 = -2
	movq $2,%r10
	negq %r10
	movl %r10d,12(%rsp)
			  #  t3 = t2 * 3
	movslq 12(%rsp),%r10
	movq $3,%r11
	imulq %r11,%r10
	movl %r10d,16(%rsp)
			  #  t4 = 1 + t3
	movq $1,%r10
	movslq 16(%rsp),%r11
	addq %r11,%r10
	movl %r10d,20(%rsp)
			  #  t5 = 0 * 4
	movq $0,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,24(%rsp)
			  #  t6 = a + t5
	movslq 4(%rsp),%r10
	movslq 24(%rsp),%r11
	addq %r11,%r10
	movl %r10d,28(%rsp)
			  #  [t6] = t4
	movslq 20(%rsp),%r10
	movslq 28(%rsp),%r11
	movl %r10d,(%r11)
			  #  t7 = false
	movq $0,%r10
	movl %r10d,32(%rsp)
			  #  if flag == false goto L1
	movslq 8(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L1
			  #  t8 = 0 * 4
	movq $0,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,36(%rsp)
			  #  t9 = a + t8
	movslq 4(%rsp),%r10
	movslq 36(%rsp),%r11
	addq %r11,%r10
	movl %r10d,40(%rsp)
			  #  t10 = [t9]
	movslq 40(%rsp),%r10
	movslq (%r10),%r11
	movl %r11d,44(%rsp)
			  #  t11 = t10 < 0
	movslq 44(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	setl %r10b
	movzbl %r10b,%r10d
	movl %r10d,48(%rsp)
			  #  t7 = t11
	movslq 48(%rsp),%r10
	movl %r10d,32(%rsp)
			  # L1:
_main_L1:
			  #  if t7 == false goto L0
	movslq 32(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L0
			  #  t12 = 1 * 4
	movq $1,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,52(%rsp)
			  #  t13 = a + t12
	movslq 4(%rsp),%r10
	movslq 52(%rsp),%r11
	addq %r11,%r10
	movl %r10d,56(%rsp)
			  #  [t13] = 4
	movq $4,%r10
	movslq 56(%rsp),%r11
	movl %r10d,(%r11)
			  # L0:
_main_L0:
			  #  t14 = 1 * 4
	movq $1,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,60(%rsp)
			  #  t15 = a + t14
	movslq 4(%rsp),%r10
	movslq 60(%rsp),%r11
	addq %r11,%r10
	movl %r10d,64(%rsp)
			  #  t16 = [t15]
	movslq 64(%rsp),%r10
	movslq (%r10),%r11
	movl %r11d,68(%rsp)
			  #  call _printInt(t16)
	movslq 68(%rsp),%rdi
	call _printInt
			  #  return 
	addq  $76,%rsp
	ret
			  # Total inst cnt: 88
