	.text
			  # _main () (x, y, z, flag)
	.p2align 4,0x90
	.globl _main
_main:
	subq $24,%rsp
			  #  flag = false
	movq $0,%r10
	movl %r10d,12(%rsp)
			  #  if flag == false goto L0
	movslq 12(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L0
			  #  x = 1
	movq $1,%r10
	movl %r10d,(%rsp)
			  # L0:
_main_L0:
			  #  if flag == false goto L1
	movslq 12(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L1
			  #  y = 1
	movq $1,%r10
	movl %r10d,4(%rsp)
			  #  goto L2
	jmp _main_L2
			  # L1:
_main_L1:
			  #  y = 2
	movq $2,%r10
	movl %r10d,4(%rsp)
			  # L2:
_main_L2:
			  # L3:
_main_L3:
			  #  if flag == false goto L4
	movslq 12(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L4
			  #  z = 1
	movq $1,%r10
	movl %r10d,8(%rsp)
			  #  goto L3
	jmp _main_L3
			  # L4:
_main_L4:
			  # L5:
_main_L5:
			  #  t1 = !flag
	movslq 12(%rsp),%r10
	notq %r10
	movl %r10d,16(%rsp)
			  #  t2 = !t1
	movslq 16(%rsp),%r10
	notq %r10
	movl %r10d,20(%rsp)
			  #  if t2 == false goto L6
	movslq 20(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L6
			  #  z = 1
	movq $1,%r10
	movl %r10d,8(%rsp)
			  #  goto L5
	jmp _main_L5
			  # L6:
_main_L6:
			  #  call _printInt(y)
	movslq 4(%rsp),%rdi
	call _printInt
			  #  return 
	addq  $24,%rsp
	ret
			  # Total inst cnt: 45
