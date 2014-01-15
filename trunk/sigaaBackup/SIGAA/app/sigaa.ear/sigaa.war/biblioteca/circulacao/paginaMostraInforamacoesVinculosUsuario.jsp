<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%-- Mantém as informações do usuário e a situação entre as requisições --%>
	<a4j:keepAlive beanName="verificaVinculosUsuarioBibliotecaMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

	<h2> <ufrn:subSistema /> &gt; Visualizar as Informações sobre os Vínculos do Usuário no Sistema </h2>

	<div class="descricaoOperacao">
		
		<c:if test="${! verificaVinculosUsuarioBibliotecaMBean.verificandoMeusVinculos}" >
			<p>Prezado operador(a),</p>
			<p>Nesta página, é possível visualizar as informações sobre <strong>todos</strong> os vínculos que o usuário selecionado possui no sistema.</p>
			<br/>
			<p>Para cada vínculo é indicado se ele permite fazer empréstimos na biblioteca ou não. Caso nenhum vínculo mostrado permita, o usuário não vai 
			poder fazer o cadastro na biblioteca ou se ele já possuir um cadastro, não poderá mais realizar empréstimos. </p>
			<p><strong>Observação: </strong>Mesmo que o vínculo permite a realização de empréstimos, se ele tiver sido quitado, não poderá mais ser usado.</p>
		</c:if>
		
		<c:if test="${verificaVinculosUsuarioBibliotecaMBean.verificandoMeusVinculos}" >
			<p>Prezado usuário(a),</p>
			<p>Nesta página é possível visualizar as informações sobre <strong>todos</strong> os vínculos que o(a) senhor(a) possui no sistema.</p>
			<br/>
			<p>Para cada vínculo é indicado se ele permite fazer empréstimos na biblioteca ou não. Caso nenhum vínculo mostrado permita, o(a) senhor(a) não vai 
			poder fazer o cadastro na biblioteca ou se já possuir um cadastro, não poderá mais realizar empréstimos.
			</p>
			<p><strong>Observação: </strong>Mesmo que o vínculo permite a realização de empréstimos, se ele tiver sido quitado, não poderá mais ser usado.</p>
		</c:if>
	</div>

	<h:form>
	
	
		<table class="visualizacao" style="width: 100%">
			
			<caption>Informações dos Vínculos</caption>
			
			
			<%-- INFORMAÇÕES PESSOAIS DO USUÁRIO  --%>
			<tr>
				<td td colspan="2">
					<c:set var="_infoUsuarioCirculacao" value="${verificaVinculosUsuarioBibliotecaMBean.infoUsuario}" scope="request" />
					<c:set var="_transparente" value="true" scope="request" />
					<c:set var="_mostrarVinculo" value="false" scope="request" />
					<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
				</td>
			</tr>
				
			
			<%-- DISCENTE --%>
			<c:if test="${ fn:length( verificaVinculosUsuarioBibliotecaMBean.infoVinculosDiscenteUsuario) > 0 }">
				
				<tr>
					<td>
							<table class="subFormulario" style="width: 100%">
								<caption>Vínculos de Discente</caption>
								
								<thead>
									<tr>
										<th style="text-align: left;width: 15%;">Matrícula</th>
										<th style="text-align: left;width: 15%;">Nível de Ensino</th>
										<th style="text-align: left;width: 35%;">Tipo de Discente</th>
										<th style="text-align: left;width: 15%;">Status do Discente</th>
										<th style="text-align: center; width: 10%;">Permite Empréstimos ? </th>
										<th style="text-align: center; width: 10%;"> </th>
									</tr>
								</thead>
								
								<c:forEach var="infoVinculo" items="${verificaVinculosUsuarioBibliotecaMBean.infoVinculosDiscenteUsuario}" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td>${infoVinculo.identificicadorVinculo}</td>
										<td>${infoVinculo.nivelCategoria}</td>
										<td>${infoVinculo.tipo}</td>
										<td>${infoVinculo.status}</td>
										<td  style="text-align: center; ${infoVinculo.podeFazerEmprestimos ? 'color:green; ' : 'color:red;'}  ${infoVinculo.podeFazerEmprestimos && infoVinculo.quitado ? 'text-decoration:line-through;' : ' ' }">
											<ufrn:format type="simnao" valor="${infoVinculo.podeFazerEmprestimos}"></ufrn:format>
										</td>
										<td>
											<c:if test="${infoVinculo.quitado}">
												<h:outputText value="[ QUITADO ]" style="color: gray; font-weight: bold;" />
											</c:if> 
										</td>
									</tr>
								</c:forEach>
								
								
								
							</table>
					</td>
				</tr>
				
			</c:if>
	
	
	
			
		    <%-- SERVIDOR --%>
			<c:if test="${ fn:length( verificaVinculosUsuarioBibliotecaMBean.infoVinculosServidorUsuario) > 0 }">
				
				<tr>
					<td>
						<table class="subFormulario" style="width: 100%">
							<caption>Vínculos de Servidor</caption>
							
							<thead>
								<tr>
									<th style="text-align: left;width: 15%;">SIAPE</th>
									<th style="text-align: left;width: 50%;">Categoria</th>
									<th style="text-align: left;width: 15%;">Status do Servidor</th>
									<th style="text-align: center; width: 10%;">Permite Empréstimos ? </th>
									<th style="text-align: center; width: 10%;"> </th>
								</tr>
							</thead>
							
							<c:forEach var="infoVinculo" items="${verificaVinculosUsuarioBibliotecaMBean.infoVinculosServidorUsuario}"  varStatus="status">
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
									
									<td>${infoVinculo.identificicadorVinculo}</td>
									<td>${infoVinculo.nivelCategoria}</td>
									<td>${infoVinculo.status}</td>
									<td style="text-align: center; ${infoVinculo.podeFazerEmprestimos ? 'color:green; ' : 'color:red;'}  ${infoVinculo.podeFazerEmprestimos && infoVinculo.quitado ? 'text-decoration:line-through;' : ' ' }">
										<ufrn:format type="simnao" valor="${infoVinculo.podeFazerEmprestimos}"></ufrn:format>
									</td>
									<td>
										<c:if test="${infoVinculo.quitado}">
											<h:outputText value="[ QUITADO ]" style="color: gray; font-weight: bold;" />
										</c:if> 
									</td>
								</tr>
							</c:forEach>
					
						
					
						</table>
					</td>
				</tr> 
				
			</c:if>
	
				
			
			
			
			
			<%-- DOCENTE EXTERNO --%>
			<c:if test="${ fn:length( verificaVinculosUsuarioBibliotecaMBean.infoVinculosDocenteExternoUsuario) > 0 }">
				
				 <tr>
					<td>
						<table class="subFormulario" style="width: 100%">
							<caption>Vínculo de Docente Externo</caption>
							
							<thead>
								<tr>
									<th style="text-align: left;width: 15%;">CPF</th>
									<th style="text-align: left;width: 15%;">Matrícula</th>
									<th style="text-align: left;width: 35%;">Prazo</th>
									<th style="text-align: left;width: 15%;">Status do Docente Externo</th>
									<th style="text-align: center; width: 10%;">Permite Empréstimos ? </th>
									<th style="text-align: center; width: 10%;"> </th>
								</tr>
							</thead>
							
							<c:forEach var="infoVinculo" items="${verificaVinculosUsuarioBibliotecaMBean.infoVinculosDocenteExternoUsuario}"  varStatus="status">
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
									<td>${infoVinculo.identificicadorVinculo}</td>
									<td>${infoVinculo.nivelCategoria}</td>
									<td>${infoVinculo.prazo}</td>
									<td>${infoVinculo.status}</td>
									<td style="text-align: center; ${infoVinculo.podeFazerEmprestimos ? 'color:green; ' : 'color:red;'}  ${infoVinculo.podeFazerEmprestimos && infoVinculo.quitado ? 'text-decoration:line-through;' : ' ' }">
										<ufrn:format type="simnao" valor="${infoVinculo.podeFazerEmprestimos}"></ufrn:format>
									</td>
									<td>
										<c:if test="${infoVinculo.quitado}">
											<h:outputText value="[ QUITADO ]" style="color: gray; font-weight: bold;" />
										</c:if>
									</td>
								</tr>
							</c:forEach>
					
						
					
						</table>
					</td>
				</tr>
				
			</c:if>
			
			
			
			
			
			<%-- USUARIO EXTERNO --%>
			<c:if test="${ fn:length( verificaVinculosUsuarioBibliotecaMBean.infoVinculosUsuarioExternoUsuario) > 0 }">
				
				<tr>
					<td>
						<table class="subFormulario" style="width: 100%">
							<caption>Vínculo de Usuário Externo</caption>
							
							<thead>
								<tr>
									<th style="text-align: left;width: 15%;">CPF</th>
									<th style="text-align: left;width: 50%;">Prazo</th>
									<th style="text-align: left;width: 15%;">Status do Usuários Externo</th>
									<th style="text-align: center; width: 10%;">Permite Empréstimos ? </th>
									<th style="text-align: center; width: 10%;"> </th>
								</tr>
							</thead>
							
							<c:forEach var="infoVinculo" items="${verificaVinculosUsuarioBibliotecaMBean.infoVinculosUsuarioExternoUsuario}" varStatus="status">
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
									
									<td>${infoVinculo.identificicadorVinculo}</td>
									<td>${infoVinculo.prazo}</td>
									<td>${infoVinculo.status}</td>
									<td  style="text-align: center; ${infoVinculo.podeFazerEmprestimos ? 'color:green; ' : 'color:red;'}  ${infoVinculo.podeFazerEmprestimos && infoVinculo.quitado ? 'text-decoration:line-through;' : ' ' }">
										<ufrn:format type="simnao" valor="${infoVinculo.podeFazerEmprestimos}"></ufrn:format>
									</td>
									<td>
										<c:if test="${infoVinculo.quitado}">
											<h:outputText value="[ QUITADO ]" style="color: gray; font-weight: bold;" />
										</c:if>
									</td>
								</tr>
							</c:forEach>
					
						
					
						</table>
					</td>
				</tr>
				
			</c:if>
			
			
			
			
			
			<tfoot>
				<tr>
					<td colspan="2" style="text-align:center;">
						<h:commandButton id="botaoVoltarVerificaVinculosBiblioteca" value="<< Voltar" action="#{buscaUsuarioBibliotecaMBean.telaBuscaUsuarioBiblioteca}" rendered="#{! verificaVinculosUsuarioBibliotecaMBean.verificandoMeusVinculos}" />
						<h:commandButton id="botaoVoltarVerificaVinculosUsuarios" value="<< Voltar" action="#{verificaVinculosUsuarioBibliotecaMBean.cancelar}" rendered="#{verificaVinculosUsuarioBibliotecaMBean.verificandoMeusVinculos}"/>
					</td>
				</tr>
			</tfoot>
				 
		</table>
		
		
	</h:form>
	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>