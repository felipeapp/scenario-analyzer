<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="cadastroUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	
	<h2> <ufrn:subSistema /> &gt; Cadastrar para Utilizar os Serviços da Biblioteca </h2>

	<div class="descricaoOperacao">
	
		<%--
			Esta página pode ser acessada por um operador da biblioteca para alterar a senha de qualquer usuário, como
			também pode ser acessada pelo próprio usuário para alterar sua senha.
		 --%>
		
		<c:if test="${!cadastroUsuarioBibliotecaMBean.operador}">
			<p>Caro usuário,</p>
			<p>Para você ter acesso aos serviços da biblioteca, selecione um vínculo e crie uma senha entre 6 dígitos e 8 dígitos (somente números), confirmando 
			  a operação com a senha que você já possui no sistema.
			</p>
			<p>Os vínculos apresentados abaixo estão ordenados de acordo com a precedência na biblioteca. </p>
			<p> <strong> Recomenda-se selecionar o primeiro vínculo, pois será o vínculo que lhe permitirá realizar uma quantidade maior de empréstimos e com maiores prazos. </strong> </p>
			<p>Caso deseje alterar sua senha basta digitar a nova senha nos campos abaixo e confirmá-la.</p>
			<br/>
			<p> <strong>Observação: </strong>Caso seja adquirido um novo vínculo, ou o vínculo atual seja cancelado, será necessário quitar a conta atual na biblioteca, para a partir desse momento, poder realizar um novo cadastro no sistema e usar um novo vínculo. </p>
		</c:if>
		
		
		<c:if test="${cadastroUsuarioBibliotecaMBean.operador}">
			<p>Caro operador,</p>
			<p>Os vínculos apresentados abaixo estão ordenados de acordo com a precedência na biblioteca. </p>
			<p><strong>Recomenda-se selecionar o primeiro vínculo, pois será o vínculo que permitirá o usuário realizar uma quantidade maior de empréstimos e com maiores prazos.</strong> </p>
			<p>Solicite ao usuário que digite sua nova senha numérica de 6 a 8 dígitos e a confirme redigitando-a no campo seguinte.</p>
			<p>A sua senha deve ser informada no final do formulário para confirmar a operação.</p>
			
		</c:if>
	</div>

	<h:form id="form">
			
		<table class="formulario" style="width:70%;">
		
				<caption> Definir Senha de Utilização da Biblioteca </caption>
				
					
				<tr>	
					<td colspan="2">
						<hr/>
					</td>
				</tr>
				
				<tr>
					<th colspan="2" style="text-align: center; color: ${cor}">Selecione um dos Vínculos Abaixo para Utilizar a Biblioteca:</th>
				</tr>
			
				<tr>
					<th colspan="2" style="text-align: center; font-weight: bold;">
						<h:selectOneMenu  id="listaComponentesCurriculares" value="#{cadastroUsuarioBibliotecaMBean.identificacaoVinculoSelecionado}" style="min-width: 300px;">
							<f:selectItems  value="#{cadastroUsuarioBibliotecaMBean.vinculosAtivosNaoUsadosComboBox}" />
						</h:selectOneMenu>	
					</th>
				</tr>
				
				<c:if test="${cadastroUsuarioBibliotecaMBean.qtdVinculosAtivosNaoUsados == 0}">
					<tr>
						<th colspan="2" style="text-align: center; color: red;"> Usuário não possui vínculos ativos que permitam utilizar a Biblioteca </th>
					</tr>
					<tr>	
						<td colspan="2" style="text-align: center;"> 
							<h:commandLink value="Verificar Informações dos Vínculos no Sistema" action="#{verificaSituacaoUsuarioBibliotecaMBean.verificaSituacaoUsuarioPassado}"
									rendered="#{cadastroUsuarioBibliotecaMBean.pessoaRetornadaBusca != null && cadastroUsuarioBibliotecaMBean.pessoaRetornadaBusca.id > 0 }">
							 	<f:param name="idPessoaVerificaSituacao" value="#{cadastroUsuarioBibliotecaMBean.pessoaRetornadaBusca.id}"></f:param>
							 </h:commandLink> 
						</td>
					</tr>
				</c:if>
				
				<tr>
					<td colspan="2">
						<hr/>
					</td>
				</tr>
					
				
				<c:if test="${cadastroUsuarioBibliotecaMBean.operador}">
					<tr>
						<th colspan="2" style="text-align: center; font-weight: bold;">Informações do Usuário</th>
					</tr>
					
					<c:if test="${not empty cadastroUsuarioBibliotecaMBean.cpfPessoa}">
						<tr>
						<th>CPF:</th>
						<td> ${cadastroUsuarioBibliotecaMBean.cpfPessoa}  </td>
						</tr>
					</c:if>
					
					<c:if test="${not empty cadastroUsuarioBibliotecaMBean.passaportePessoa}">
						<tr>
						<th>Passaporte:</th>
						<td> ${cadastroUsuarioBibliotecaMBean.passaportePessoa}  </td>
						</tr>
					</c:if>
					
					<tr>
						<th>Nome:</th>
						<td> ${cadastroUsuarioBibliotecaMBean.nomePessoa}  </td>
					</tr>
					
					<c:if test="${not empty cadastroUsuarioBibliotecaMBean.dataNascimento}">
						<tr>
							<th>Data de Nascimento:</th>
							<td> ${cadastroUsuarioBibliotecaMBean.dataNascimento}  </td>
						</tr>
					</c:if>
					
				</c:if>
			
				<c:if test="${ cadastroUsuarioBibliotecaMBean.usuarioBibliotecaAtual == null && cadastroUsuarioBibliotecaMBean.qtdVinculosAtivosNaoUsados > 0 && ! cadastroUsuarioBibliotecaMBean.vinculoAtualExpirado
				 			|| ( cadastroUsuarioBibliotecaMBean.usuarioBibliotecaAtual != null  && ! cadastroUsuarioBibliotecaMBean.vinculoAtualExpirado ) }">
			
					<tr>
						<th nowrap="nowrap"> Digite a senha: </th>
						<td> 
							<input type="password" id="senha1" name="senha1" size="12" maxlength="8" onkeyup="formatarInteiro(this)" /> (somente números) 
						</td>
					</tr>
					<tr>
						<th nowrap="nowrap"> Confirme a senha: </th>
						<td>
							 <input type="password" id="senha2" name="senha2" size="12" maxlength="8" onkeyup="formatarInteiro(this)" />
						 </td>
					</tr>
					
					<c:if test="${!cadastroUsuarioBibliotecaMBean.operador}">
						<tr>
							<th nowrap="nowrap"> Entre com a sua senha do Sistema: </th>
							<td>  
								<input type="password" id="senhaSigaa" name="senhaSigaa" size="30" maxlength="20" /> 
							</td>
						</tr>
					</c:if>
					
					<c:if test="${cadastroUsuarioBibliotecaMBean.operador}">
						<tr>
							<td colspan="2">
								<div id="senhaOperador">
									<c:set var="exibirApenasSenha" value="true" scope="request"/>
									<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
								</div>
							</td>
						</tr>
						
						
						<c:if test="${cadastroUsuarioBibliotecaMBean.cadastrarNovoUsuario && cadastroUsuarioBibliotecaMBean.termoDeResponsabilidadeAtivo}">
							<tr>
								<td colspan="2" style="color: red; text-align: center;">
									<strong>IMPORTANTE:</strong> Caso seja realizado o cadastro do usuário pelo operador de circulação, o termo de adesão não será assinado.
								</td>
							</tr>
						</c:if>
					
					</c:if>
					
					
				
				</c:if>
				
				<tfoot>
					<tr>
						<td colspan="2" align="center">
							<h:commandButton value="Cadastrar" action="#{cadastroUsuarioBibliotecaMBean.cadastrar}" 
									rendered="#{cadastroUsuarioBibliotecaMBean.usuarioBibliotecaAtual == null && cadastroUsuarioBibliotecaMBean.qtdVinculosAtivosNaoUsados > 0 && ! cadastroUsuarioBibliotecaMBean.vinculoAtualExpirado  && cadastroUsuarioBibliotecaMBean.pessoaRetornadaBusca != null }" />
							<h:commandButton value="Alterar" action="#{cadastroUsuarioBibliotecaMBean.cadastrar}" 
								rendered="#{cadastroUsuarioBibliotecaMBean.usuarioBibliotecaAtual != null && ! cadastroUsuarioBibliotecaMBean.vinculoAtualExpirado && cadastroUsuarioBibliotecaMBean.pessoaRetornadaBusca != null }" />
							
							<h:commandButton value="<< Voltar" action="#{buscaUsuarioBibliotecaMBean.telaBuscaUsuarioBiblioteca}" rendered="#{cadastroUsuarioBibliotecaMBean.operador}" />
							<h:commandButton value="Cancelar" action="#{cadastroUsuarioBibliotecaMBean.cancelar}" immediate="true" rendered="true" onclick="#{confirm}" />
						</td>
					</tr>
				</tfoot>
		</table>
		
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>