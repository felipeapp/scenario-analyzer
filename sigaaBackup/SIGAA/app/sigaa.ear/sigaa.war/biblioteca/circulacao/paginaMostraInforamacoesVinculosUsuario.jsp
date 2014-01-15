<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%-- Mant�m as informa��es do usu�rio e a situa��o entre as requisi��es --%>
	<a4j:keepAlive beanName="verificaVinculosUsuarioBibliotecaMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

	<h2> <ufrn:subSistema /> &gt; Visualizar as Informa��es sobre os V�nculos do Usu�rio no Sistema </h2>

	<div class="descricaoOperacao">
		
		<c:if test="${! verificaVinculosUsuarioBibliotecaMBean.verificandoMeusVinculos}" >
			<p>Prezado operador(a),</p>
			<p>Nesta p�gina, � poss�vel visualizar as informa��es sobre <strong>todos</strong> os v�nculos que o usu�rio selecionado possui no sistema.</p>
			<br/>
			<p>Para cada v�nculo � indicado se ele permite fazer empr�stimos na biblioteca ou n�o. Caso nenhum v�nculo mostrado permita, o usu�rio n�o vai 
			poder fazer o cadastro na biblioteca ou se ele j� possuir um cadastro, n�o poder� mais realizar empr�stimos. </p>
			<p><strong>Observa��o: </strong>Mesmo que o v�nculo permite a realiza��o de empr�stimos, se ele tiver sido quitado, n�o poder� mais ser usado.</p>
		</c:if>
		
		<c:if test="${verificaVinculosUsuarioBibliotecaMBean.verificandoMeusVinculos}" >
			<p>Prezado usu�rio(a),</p>
			<p>Nesta p�gina � poss�vel visualizar as informa��es sobre <strong>todos</strong> os v�nculos que o(a) senhor(a) possui no sistema.</p>
			<br/>
			<p>Para cada v�nculo � indicado se ele permite fazer empr�stimos na biblioteca ou n�o. Caso nenhum v�nculo mostrado permita, o(a) senhor(a) n�o vai 
			poder fazer o cadastro na biblioteca ou se j� possuir um cadastro, n�o poder� mais realizar empr�stimos.
			</p>
			<p><strong>Observa��o: </strong>Mesmo que o v�nculo permite a realiza��o de empr�stimos, se ele tiver sido quitado, n�o poder� mais ser usado.</p>
		</c:if>
	</div>

	<h:form>
	
	
		<table class="visualizacao" style="width: 100%">
			
			<caption>Informa��es dos V�nculos</caption>
			
			
			<%-- INFORMA��ES PESSOAIS DO USU�RIO  --%>
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
								<caption>V�nculos de Discente</caption>
								
								<thead>
									<tr>
										<th style="text-align: left;width: 15%;">Matr�cula</th>
										<th style="text-align: left;width: 15%;">N�vel de Ensino</th>
										<th style="text-align: left;width: 35%;">Tipo de Discente</th>
										<th style="text-align: left;width: 15%;">Status do Discente</th>
										<th style="text-align: center; width: 10%;">Permite Empr�stimos ? </th>
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
							<caption>V�nculos de Servidor</caption>
							
							<thead>
								<tr>
									<th style="text-align: left;width: 15%;">SIAPE</th>
									<th style="text-align: left;width: 50%;">Categoria</th>
									<th style="text-align: left;width: 15%;">Status do Servidor</th>
									<th style="text-align: center; width: 10%;">Permite Empr�stimos ? </th>
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
							<caption>V�nculo de Docente Externo</caption>
							
							<thead>
								<tr>
									<th style="text-align: left;width: 15%;">CPF</th>
									<th style="text-align: left;width: 15%;">Matr�cula</th>
									<th style="text-align: left;width: 35%;">Prazo</th>
									<th style="text-align: left;width: 15%;">Status do Docente Externo</th>
									<th style="text-align: center; width: 10%;">Permite Empr�stimos ? </th>
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
							<caption>V�nculo de Usu�rio Externo</caption>
							
							<thead>
								<tr>
									<th style="text-align: left;width: 15%;">CPF</th>
									<th style="text-align: left;width: 50%;">Prazo</th>
									<th style="text-align: left;width: 15%;">Status do Usu�rios Externo</th>
									<th style="text-align: center; width: 10%;">Permite Empr�stimos ? </th>
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