<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style type="text/css">
#divCodigoVerificacao{
	text-align: center;
	margin-right: auto;
	margin-left: auto;
	font-style: italic;
	color: gray;
}

.tabelaRelatorio tbody tr th {
	text-align: right;
}
</style>

<f:view>

	<h2>Comprovante de solicitação de catalogação na Fonte</h2>

	<table class="tabelaRelatorio" width="100%">
	
		<tbody>
		
			<tr>
				<th width="40%">Número da Solicitação:</th>
				<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.numeroSolicitacao}"/></td>
			</tr>
		
			<c:if test="${not empty solicitacaoCatalogacaoMBean.obj.discente}">
				
				<tr>
					<th width="40%">Solicitante:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.discente}"/></td>
				</tr>
				
				<th>Categoria:</th>
				
				<c:if test="${  ( solicitacaoCatalogacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						|| solicitacaoCatalogacaoMBean.obj.discente.lato 
						|| solicitacaoCatalogacaoMBean.obj.discente.mestrado 
						|| solicitacaoCatalogacaoMBean.obj.discente.doutorado}">
					
					<td>Aluno de Pós-Graduação</td>
				</c:if>
				<c:if test="${ ! ( solicitacaoCatalogacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						&& !solicitacaoCatalogacaoMBean.obj.discente.lato 
						&& !solicitacaoCatalogacaoMBean.obj.discente.mestrado 
						&& !solicitacaoCatalogacaoMBean.obj.discente.doutorado}">
					<td>Aluno de Graduação</td>
				</c:if>
				
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.discente.curso}"/></td>
				</tr>
				
			</c:if>
			
			<c:if test="${not empty solicitacaoCatalogacaoMBean.obj.servidor}">
				<tr>
					<th>Solicitante:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.servidor}"/></td>
				</tr>
				<tr>
					<th>Categoria:</th>
					<td>
						<c:if test="${solicitacaoCatalogacaoMBean.obj.servidor.categoria.docente}" >
							Docente
						</c:if>
						<c:if test="${solicitacaoCatalogacaoMBean.obj.servidor.categoria.tecnico}" >
							Técnico Administrativo
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Lotação:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.servidor.unidade}"/></td>
				</tr>
			</c:if>
		
			<tr>
				<th>Telefone:</th>
				<td> ${solicitacaoCatalogacaoMBean.obj.pessoa.telefone} </td>
			</tr>
			
			<tr>
				<th>Celular:</th>
				<td> ${solicitacaoCatalogacaoMBean.obj.pessoa.celular} </td>
			</tr>
			
			<tr>
				<th>Email:</th>
				<td> ${solicitacaoCatalogacaoMBean.obj.pessoa.email} </td>
			</tr>
			
			<tr>
				<th>Data da Solicitação:</th>
				<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.dataCadastro}"/></td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.biblioteca.descricao}"/></td>
			</tr>
			<tr>
				<th>Tipo do Documento:</th>
				<td>
					<h:outputText value="#{solicitacaoCatalogacaoMBean.obj.tipoDocumento.denominacao}"/>
					<c:if test="${solicitacaoCatalogacaoMBean.obj.tipoDocumento.tipoDocumentoOutro}">
							(  ${solicitacaoCatalogacaoMBean.obj.outroTipoDocumento}  )
					</c:if>	
				</td>
			</tr>
			
			<tr>
				<th style="vertical-align:top;">Número de folhas:</th>
				<td>
					<h:outputText value="#{solicitacaoCatalogacaoMBean.obj.numeroPaginas}"/>
				</td>
			</tr>
			
			<tr>
				<th>Palavras-chave:</th>
				<td>
					<h:outputText id="palavrasChaves" value="#{solicitacaoCatalogacaoMBean.obj.palavrasChaveString}"/>
				</td>
			</tr>
			
			<%-- <tr>
				<th>Autoriza Descarte?</th>
				<td>
					<h:outputText value="#{solicitacaoCatalogacaoMBean.obj.autorizaDescarte}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
				</td>
			</tr> --%>
		</tbody>
	</table>
	
	<div id="divCodigoVerificacao">
			Código de Autenticação : ${solicitacaoCatalogacaoMBean.numeroAutenticacao}
	</div>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>