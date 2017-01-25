	.text
			  # _main () (b, x)
	.p2align 4,0x90
	.globl _main
_main:
	subq $24,%rsp
			  #  b = false
	movq $0,%r10
	movl %r10d,(%rsp)
			  #  t1 = true
	movq $1,%r10
	movl %r10d,8(%rsp)
			  #  if true == true goto L1
	movq $1,%r10
	movq $1,%r11
	cmpq %r11,%r10
	je _main_L1
			  #  t1 = b
	movslq (%rsp),%r10
	movl %r10d,8(%rsp)
			  # L1:
_main_L1:
			  #  if t1 == false goto L0
	movslq 8(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L0
			  #  x = 1
	movq $1,%r10
	movl %r10d,4(%rsp)
			  #  goto L2
	jmp _main_L2
			  # L0:
_main_L0:
			  #  x = 2
	movq $2,%r10
	movl %r10d,4(%rsp)
			  # L2:
_main_L2:
			  #  call _printInt(x)
	movslq 4(%rsp),%rdi
	call _printInt
			  #  return 
	addq  $24,%rsp
	ret
			  # Total inst cnt: 27
