
<%--   para de ressultados da busca padrão de usuários do sistema   --%>

<jsp:useBean id="buscaPessoaBibliotecaDao" class="br.ufrn.sigaa.arq.dao.biblioteca.BuscaPessoaBibliotecaDao" scope="request"/>



<c:if test="${ buscaUsuarioBibliotecaMBean.quantidadeUsuarios > 0 }">
	
	
		
	
	<div class="infoAltRem" style="margin-top: 10px;">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
		Selecionar o Usuário
	</div> 



	<div style="margin-top:30px">
		
		<table class="listagem">
			<caption> Usuários Encontrados ( ${buscaUsuarioBibliotecaMBean.quantidadeUsuarios} ) </caption>
			
			
			<thead>
				<tr>
					
					<c:if test="${buscaUsuarioBibliotecaMBean.mostarDadosBiblioteca}">
						<th colspan="4">Descrição</th>
						<th style="width:20px;"></th>
					</c:if>
					<c:if test="${buscaUsuarioBibliotecaMBean.mostrarDadosPessoa }">
						<th style="text-align:center;">CPF / Passaporte</th>
						<th>Nome</th>
						<th style="text-align:center;">Data de Nascimento</th>
						<th style="width:20px;"></th>
					</c:if>
				</tr>
			
			</thead>
			
			<tbody>


				<c:forEach items="#{buscaUsuarioBibliotecaMBean.infoPessoas}" var="infoPessoa" varStatus="status">

					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">

						
						<%-- Mostra dos dados os usuário selecionados --%>

						<c:if test="${buscaUsuarioBibliotecaMBean.mostrarDadosPessoa }">
							
							<td style="text-align:center;">
							
								<c:if test="${not empty infoPessoa[buscaPessoaBibliotecaDao.posicaoCpf]}">
									${infoPessoa[ buscaPessoaBibliotecaDao.posicaoCpf ]} (CPF)
								</c:if>	
								<c:if test="${empty infoPessoa[buscaPessoaBibliotecaDao.posicaoCpf]}">
								
									 <c:if test="${ not empty infoPessoa[buscaPessoaBibliotecaDao.posicaoPassaporte] }">
										${infoPessoa[ buscaPessoaBibliotecaDao.posicaoPassaporte ]} (Passaporte)
									 </c:if>
									 <c:if test="${ empty infoPessoa[buscaPessoaBibliotecaDao.posicaoPassaporte] }">
										---
									 </c:if>
								</c:if>		
							</td>
							
							<td>
								${infoPessoa[buscaPessoaBibliotecaDao.posicaoNome]}
							</td>
							
							<td style="text-align:center;">
								<ufrn:format type="data" valor="${infoPessoa[buscaPessoaBibliotecaDao.posicaoDataNascimento]}"/>
							</td>

						</c:if>
						
						
						<c:if test="${buscaUsuarioBibliotecaMBean.mostarDadosBiblioteca}">
							<td style="width: 98%" >
								${infoPessoa[ buscaPessoaBibliotecaDao.posicaoIdentificadorBiblioteca ]} - ${infoPessoa[ buscaPessoaBibliotecaDao.posicaoDescricaoBiblioteca ]}
							</td>
						</c:if>		
	
						
						<%-- A ação de selecionar um usuário e seus vários parametros passandos--%>
						
						<td style="width: 1%;">	
							<h:commandLink id="cmdLinkSelecionarUsuarioBuscaPadrao" action="#{buscaUsuarioBibliotecaMBean.selecionouUsuario}">
								<h:graphicImage url="/img/seta.gif" style="border:none" title="Selecionar este Usuário" />
								<c:if test="${buscaUsuarioBibliotecaMBean.mostrarDadosPessoa }">
									<f:param name="idPessoaSelecionada" value="#{infoPessoa[buscaPessoaBibliotecaDao.posicaoIdPessoa]}" />
									<f:param name="cpfPessoaSelecionada" value="#{infoPessoa[buscaPessoaBibliotecaDao.posicaoCpf]}" />
									<f:param name="passaportePessoaSelecionada" value="#{infoPessoa[buscaPessoaBibliotecaDao.posicaoPassaporte]}" />
									<f:param name="nomePessoaSelecionada" value="#{infoPessoa[buscaPessoaBibliotecaDao.posicaoNome]}" />
									<c:if test="${not empty infoPessoa[buscaPessoaBibliotecaDao.posicaoIdUsuarioBiblioteca] }">
										<f:param name="idUsuarioBibliotecaSelecionado" value="#{infoPessoa[buscaPessoaBibliotecaDao.posicaoIdUsuarioBiblioteca]}" />
									</c:if>
									<c:if test="${not empty infoPessoa[buscaPessoaBibliotecaDao.posicaoDataNascimento] }">
										<f:param name="dataNascimentoUsuarioSelecionado" value="#{infoPessoa[buscaPessoaBibliotecaDao.posicaoDataNascimento]}" />
									</c:if>
								</c:if>
								<c:if test="${buscaUsuarioBibliotecaMBean.mostarDadosBiblioteca }">
									<f:param name="idBibliotecaSelecionada" value="#{infoPessoa[buscaPessoaBibliotecaDao.posicaoIdBiblioteca]}" />
									<f:param name="identificadorBibliotecaSelecionada" value="#{infoPessoa[buscaPessoaBibliotecaDao.posicaoIdentificadorBiblioteca]}" />
									<f:param name="descricaoBibliotecaSelecionada" value="#{infoPessoa[buscaPessoaBibliotecaDao.posicaoDescricaoBiblioteca]}" />
									<c:if test="${not empty infoPessoa[buscaPessoaBibliotecaDao.posicaoIdUsuarioBibliotecaBiblioteca] }">
										<f:param name="idUsuarioBibliotecaSelecionado" value="#{infoPessoa[buscaPessoaBibliotecaDao.posicaoIdUsuarioBibliotecaBiblioteca]}" />
									</c:if>
								</c:if>
							</h:commandLink>
						</td>
						
					</tr>
				</c:forEach>
				
			</tbody>
			
		</table>
	</div>

</c:if>

