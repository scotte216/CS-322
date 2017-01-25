	.text
			  # _main () (a, b, x)
	.p2align 4,0x90
	.globl _main
_main:
	subq $28,%rsp
			  #  a = 0
	movq $0,%r10
	movl %r10d,(%rsp)
			  #  b = 0
	movq $0,%r10
	movl %r10d,4(%rsp)
			  #  t2 = true
	movq $1,%r10
	movl %r10d,12(%rsp)
			  #  t1 = a > 0
	movslq (%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %r10d,16(%rsp)
			  #  if t1 == true goto L1
	movslq 16(%rsp),%r10
	movq $1,%r11
	cmpq %r11,%r10
	je _main_L1
			  #  t3 = b > 0
	movslq 4(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %r10d,20(%rsp)
			  #  t2 = t3
	movslq 20(%rsp),%r10
	movl %r10d,12(%rsp)
			  # L1:
_main_L1:
			  #  if t2 == false goto L0
	movslq 12(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L0
			  #  x = 1
	movq $1,%r10
	movl %r10d,8(%rsp)
			  #  goto L2
	jmp _main_L2
			  # L0:
_main_L0:
			  #  x = 2
	movq $2,%r10
	movl %r10d,8(%rsp)
			  # L2:
_main_L2:
			  #  call _printInt(x)
	movslq 8(%rsp),%rdi
	call _printInt
			  #  return 
	addq  $28,%rsp
	ret
			  # Total inst cnt: 41
