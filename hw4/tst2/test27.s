	.text
			  # _main () (b, x)
	.p2align 4,0x90
	.globl _main
_main:
	subq $24,%rsp
			  #  b = false
	movq $0,%r10
	movl %r10d,(%rsp)
			  #  t2 = true
	movq $1,%r10
	movl %r10d,8(%rsp)
			  #  t1 = true
	movq $1,%r10
	movl %r10d,12(%rsp)
			  #  if false == true goto L1
	movq $0,%r10
	movq $1,%r11
	cmpq %r11,%r10
	je _main_L1
			  #  t1 = true
	movq $1,%r10
	movl %r10d,12(%rsp)
			  # L1:
_main_L1:
			  #  if t1 == true goto L2
	movslq 12(%rsp),%r10
	movq $1,%r11
	cmpq %r11,%r10
	je _main_L2
			  #  t2 = b
	movslq (%rsp),%r10
	movl %r10d,8(%rsp)
			  # L2:
_main_L2:
			  #  if t2 == false goto L0
	movslq 8(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L0
			  #  x = 1
	movq $1,%r10
	movl %r10d,4(%rsp)
			  #  goto L3
	jmp _main_L3
			  # L0:
_main_L0:
			  #  x = 2
	movq $2,%r10
	movl %r10d,4(%rsp)
			  # L3:
_main_L3:
			  #  call _printInt(x)
	movslq 4(%rsp),%rdi
	call _printInt
			  #  return 
	addq  $24,%rsp
	ret
			  # Total inst cnt: 35
