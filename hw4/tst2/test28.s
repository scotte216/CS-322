	.text
			  # _main () (x)
	.p2align 4,0x90
	.globl _main
_main:
	subq $24,%rsp
			  #  t2 = false
	movq $0,%r10
	movl %r10d,4(%rsp)
			  #  t1 = true
	movq $1,%r10
	movl %r10d,8(%rsp)
			  #  if false == true goto L1
	movq $0,%r10
	movq $1,%r11
	cmpq %r11,%r10
	je _main_L1
			  #  t1 = true
	movq $1,%r10
	movl %r10d,8(%rsp)
			  # L1:
_main_L1:
			  #  if t1 == false goto L2
	movslq 8(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L2
			  #  t3 = !false
	movq $0,%r10
	notq %r10
	movl %r10d,12(%rsp)
			  #  t2 = t3
	movslq 12(%rsp),%r10
	movl %r10d,4(%rsp)
			  # L2:
_main_L2:
			  #  if t2 == false goto L0
	movslq 4(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L0
			  #  x = 1
	movq $1,%r10
	movl %r10d,(%rsp)
			  #  goto L3
	jmp _main_L3
			  # L0:
_main_L0:
			  #  x = 2
	movq $2,%r10
	movl %r10d,(%rsp)
			  # L3:
_main_L3:
			  #  call _printInt(x)
	movslq (%rsp),%rdi
	call _printInt
			  #  return 
	addq  $24,%rsp
	ret
			  # Total inst cnt: 36
