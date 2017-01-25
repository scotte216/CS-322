	.text
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
	subq $100,%rsp
			  #  t1 = call _malloc(8)
	movq $8,%rdi
	call _malloc
	movl %eax,(%rsp)
			  #  a = t1
	movslq (%rsp),%r10
	movl %r10d,4(%rsp)
			  #  t2 = -2
	movq $2,%r10
	negq %r10
	movl %r10d,8(%rsp)
			  #  t3 = t2 * 3
	movslq 8(%rsp),%r10
	movq $3,%r11
	imulq %r11,%r10
	movl %r10d,12(%rsp)
			  #  t4 = 1 + t3
	movq $1,%r10
	movslq 12(%rsp),%r11
	addq %r11,%r10
	movl %r10d,16(%rsp)
			  #  t5 = 0 * 4
	movq $0,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,20(%rsp)
			  #  t6 = a + t5
	movslq 4(%rsp),%r10
	movslq 20(%rsp),%r11
	addq %r11,%r10
	movl %r10d,24(%rsp)
			  #  [t6] = t4
	movslq 16(%rsp),%r10
	movslq 24(%rsp),%r11
	movl %r10d,(%r11)
			  #  t7 = false
	movq $0,%r10
	movl %r10d,28(%rsp)
			  #  if true == false goto L0
	movq $1,%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L0
			  #  t8 = 0 * 4
	movq $0,%r10
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
			  #  t11 = t10 < 0
	movslq 40(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	setl %r10b
	movzbl %r10b,%r10d
	movl %r10d,44(%rsp)
			  #  t7 = t11
	movslq 44(%rsp),%r10
	movl %r10d,28(%rsp)
			  # L0:
_main_L0:
			  #  flag = t7
	movslq 28(%rsp),%r10
	movl %r10d,48(%rsp)
			  #  if flag == false goto L1
	movslq 48(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L1
			  #  t12 = 0 * 4
	movq $0,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,52(%rsp)
			  #  t13 = a + t12
	movslq 4(%rsp),%r10
	movslq 52(%rsp),%r11
	addq %r11,%r10
	movl %r10d,56(%rsp)
			  #  t14 = [t13]
	movslq 56(%rsp),%r10
	movslq (%r10),%r11
	movl %r11d,60(%rsp)
			  #  t15 = 1 * 4
	movq $1,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,64(%rsp)
			  #  t16 = a + t15
	movslq 4(%rsp),%r10
	movslq 64(%rsp),%r11
	addq %r11,%r10
	movl %r10d,68(%rsp)
			  #  [t16] = t14
	movslq 60(%rsp),%r10
	movslq 68(%rsp),%r11
	movl %r10d,(%r11)
			  #  goto L2
	jmp _main_L2
			  # L1:
_main_L1:
			  #  t17 = 1 * 4
	movq $1,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,72(%rsp)
			  #  t18 = a + t17
	movslq 4(%rsp),%r10
	movslq 72(%rsp),%r11
	addq %r11,%r10
	movl %r10d,76(%rsp)
			  #  [t18] = 0
	movq $0,%r10
	movslq 76(%rsp),%r11
	movl %r10d,(%r11)
			  # L2:
_main_L2:
			  #  t19 = 1 * 4
	movq $1,%r10
	movq $4,%r11
	imulq %r11,%r10
	movl %r10d,80(%rsp)
			  #  t20 = a + t19
	movslq 4(%rsp),%r10
	movslq 80(%rsp),%r11
	addq %r11,%r10
	movl %r10d,84(%rsp)
			  #  t21 = [t20]
	movslq 84(%rsp),%r10
	movslq (%r10),%r11
	movl %r11d,88(%rsp)
			  #  call _printInt(t21)
	movslq 88(%rsp),%rdi
	call _printInt
			  #  return 
	addq  $100,%rsp
	ret
			  # Total inst cnt: 111
