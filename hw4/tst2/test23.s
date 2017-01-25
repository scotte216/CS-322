	.text
			  # _main () (x, y)
	.p2align 4,0x90
	.globl _main
_main:
	subq $36,%rsp
			  #  t2 = false
	movq $0,%r10
	movl %r10d,8(%rsp)
			  #  t1 = 1 == 1
	movq $1,%r10
	movq $1,%r11
	cmpq %r11,%r10
	sete %r10b
	movzbl %r10b,%r10d
	movl %r10d,12(%rsp)
			  #  if t1 == false goto L0
	movslq 12(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L0
			  #  t3 = 2 > 4
	movq $2,%r10
	movq $4,%r11
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
			  #  t5 = false
	movq $0,%r10
	movl %r10d,20(%rsp)
			  #  t4 = 2 != 2
	movq $2,%r10
	movq $2,%r11
	cmpq %r11,%r10
	setne %r10b
	movzbl %r10b,%r10d
	movl %r10d,24(%rsp)
			  #  if t4 == false goto L1
	movslq 24(%rsp),%r10
	movq $0,%r11
	cmpq %r11,%r10
	je _main_L1
			  #  t5 = x
	movslq (%rsp),%r10
	movl %r10d,20(%rsp)
			  # L1:
_main_L1:
			  #  y = t5
	movslq 20(%rsp),%r10
	movl %r10d,4(%rsp)
			  #  call _printBool(x)
	movslq (%rsp),%rdi
	call _printBool
			  #  call _printBool(y)
	movslq 4(%rsp),%rdi
	call _printBool
			  #  return 
	addq  $36,%rsp
	ret
			  # Total inst cnt: 48
