<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<c:set var="totalUsuarios" value="0" />
	<c:set var="totalMateriais" value="0" />

	<table class="tabelaRelatorioBorda" style="width:100%;margin-bottom:30px; border-top:none;">
		<thead>
			<tr>
				<th>Nome</th>
				<th>Identificação</th>
				<th>Código de Barras</th>
				<th>Material</th>
				<th style="text-align: center;">Prazo</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${ !_abstractRelatorioBiblioteca.relatorioUsuariosEmAtraso 
					|| ( _abstractRelatorioBiblioteca.relatorioUsuariosEmAtraso  && !_abstractRelatorioBiblioteca.consultaServidor ) }">
				<c:forEach var="ctg" items="#{ _abstractRelatorioBiblioteca.resultados }" >
					<tr><td colspan="5" style="background-color: #DDDDDD;">${ ctg.key }</td></tr>
					<c:forEach var="bibl" items="#{ ctg.value }" >
						<tr><td colspan="5" style="background-color: #EEEEEE;">${ bibl.key }</td></tr>
						<c:forEach var="linha" items="#{ bibl.value }" >
							
							<c:set var="totalMateriais" value="${ totalMateriais + 1 }" />
							
							<tr>
								<c:if test="${ linha.nome != usuarioAnterior }">
									<td>
										${ fn:substring( linha.nome, 0, 60 ) }${ (fn:length(linha.nome) > 60 ? '...' : '') }
									</td>
									<td>
										<c:if test="${not empty linha.matricula}">
											Matrícula: ${linha.matricula}
										</c:if>
										<c:if test="${not empty linha.siape}">
											SIAPE: ${linha.siape}
										</c:if>
										<c:if test="${empty linha.matricula and empty linha.siape and not empty linha.cpfCnpj}">
											CPF: ${linha.cpfCnpj}
										</c:if>
										<c:if test='${linha.usuarioExterno}'>
											Usuário Externo
										</c:if>
									</td>
								
									<c:set var="totalUsuarios" value="${ totalUsuarios + 1 }" />
								</c:if>
								<c:if test="${ linha.nome == usuarioAnterior }">
									<td colspan="2"></td>
								</c:if>
								
								<td>
									<c:if test="${not empty linha.dataEmprestimo}">
										${ linha.codigoBarras }
									</c:if>
								</td>
								<td>
									<c:if test="${not empty linha.titulo}">
										${ fn:substring( linha.titulo, 0, 40 ) }${ fn:length(linha.titulo) > 40 ? '...' : '' }
									</c:if>
								</td>
								<td>
									<c:if test="${not empty linha.prazo}">
										<ufrn:format type="data" valor="${linha.prazo}" />
									</c:if>
								</td>
							</tr>
							
							<c:set var="usuarioAnterior" value="#{ linha.nome }" />
							
						</c:forEach>
					</c:forEach>
				</c:forEach>
			</c:if>
			<c:if test="${_abstractRelatorioBiblioteca.relatorioUsuariosEmAtraso && _abstractRelatorioBiblioteca.consultaServidor}">
				<c:forEach var="unid" items="#{ _abstractRelatorioBiblioteca.resultadosServidores }" >
					<tr><td colspan="5" style="background-color: #CCCCCC;">${ unid.key }</td></tr>
					<c:forEach var="ctg" items="#{ unid.value }" >
						<tr><td colspan="5" style="background-color: #DDDDDD;">${ ctg.key }</td></tr>
						<c:forEach var="bibl" items="#{ ctg.value }" >
							<tr><td colspan="5" style="background-color: #EEEEEE;">${ bibl.key }</td></tr>
							<c:forEach var="linha" items="#{ bibl.value }" >
								
								<c:set var="totalMateriais" value="${ totalMateriais + 1 }" />
								
								<tr>
									<c:if test="${ linha.nome != usuarioAnterior }">
										<td>
											${ fn:substring( linha.nome, 0, 60 ) }${ (fn:length(linha.nome) > 60 ? '...' : '') }
										</td>
										<td>
											<c:if test="${not empty linha.matricula}">
												Matrícula: ${linha.matricula}
											</c:if>
											<c:if test="${not empty linha.siape}">
												SIAPE: ${linha.siape}
											</c:if>
											<c:if test="${empty linha.matricula and empty linha.siape and not empty linha.cpfCnpj}">
												CPF: ${linha.cpfCnpj}
											</c:if>
											<c:if test='${linha.usuarioExterno}'>
												Usuário Externo
											</c:if>
										</td>
									
										<c:set var="totalUsuarios" value="${ totalUsuarios + 1 }" />
									</c:if>
									<c:if test="${ linha.nome == usuarioAnterior }">
										<td colspan="2"></td>
									</c:if>
									
									<td>
										<c:if test="${not empty linha.dataEmprestimo}">
											${ linha.codigoBarras }
										</c:if>
									</td>
									<td>
										<c:if test="${not empty linha.titulo}">
											${ fn:substring( linha.titulo, 0, 40 ) }${ fn:length(linha.titulo) > 40 ? '...' : '' }
										</c:if>
									</td>
									<td>
										<c:if test="${not empty linha.prazo}">
											<ufrn:format type="data" valor="${linha.prazo}" />
										</c:if>
									</td>
								</tr>
								
								<c:set var="usuarioAnterior" value="#{ linha.nome }" />
								
							</c:forEach>
						</c:forEach>
					</c:forEach>
				</c:forEach>
			</c:if>
		</tbody>
	</table>

	<h3>
		Total de usuários: ${ totalUsuarios } <br/>
		Total de empréstimos atrasados: ${ totalMateriais }
	</h3>
	
</f:view>


<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>