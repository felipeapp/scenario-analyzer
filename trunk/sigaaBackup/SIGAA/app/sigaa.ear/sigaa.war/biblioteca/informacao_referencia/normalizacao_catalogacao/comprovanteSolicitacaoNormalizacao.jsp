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

	<h2>Comprovante de solicita��o de normaliza��o</h2>

	<table class="tabelaRelatorio" width="100%">
	
		<tbody>
		
			<tr>
				<th width="40%">N�mero da Solicita��o:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.numeroSolicitacao}"/></td>
			</tr>
		
			<c:if test="${not empty solicitacaoNormalizacaoMBean.obj.discente}">
				
				<tr>
					<th width="40%">Solicitante:</th>
					<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.discente}"/></td>
				</tr>
				
				<th>Categoria:</th>
				
				<c:if test="${  ( solicitacaoNormalizacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						|| solicitacaoNormalizacaoMBean.obj.discente.lato 
						|| solicitacaoNormalizacaoMBean.obj.discente.mestrado 
						|| solicitacaoNormalizacaoMBean.obj.discente.doutorado}">
					
					<td>Aluno de P�s-Gradua��o</td>
				</c:if>
				<c:if test="${ ! ( solicitacaoNormalizacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						&& !solicitacaoNormalizacaoMBean.obj.discente.lato 
						&& !solicitacaoNormalizacaoMBean.obj.discente.mestrado 
						&& !solicitacaoNormalizacaoMBean.obj.discente.doutorado}">
					<td>Aluno de Gradua��o</td>
				</c:if>
				
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.discente.curso}"/></td>
				</tr>
				
			</c:if>
			
			<c:if test="${not empty solicitacaoNormalizacaoMBean.obj.servidor}">
				<tr>
					<th>Solicitante:</th>
					<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.servidor}"/></td>
				</tr>
				<tr>
					<th>Categoria:</th>
					<td>
						<c:if test="${solicitacaoNormalizacaoMBean.obj.servidor.categoria.docente}" >
							Docente
						</c:if>
						<c:if test="${solicitacaoNormalizacaoMBean.obj.servidor.categoria.tecnico}" >
							T�cnico Administrativo
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Lota��o:</th>
					<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.servidor.unidade}"/></td>
				</tr>
			</c:if>
		
			<tr>
				<th>Telefone:</th>
				<td> ${solicitacaoNormalizacaoMBean.obj.pessoa.telefone} </td>
			</tr>
			
			<tr>
				<th>Celular:</th>
				<td> ${solicitacaoNormalizacaoMBean.obj.pessoa.celular} </td>
			</tr>
			
			<tr>
				<th>Email:</th>
				<td> ${solicitacaoNormalizacaoMBean.obj.pessoa.email} </td>
			</tr>
			
			<tr>
				<th>Data da Solicita��o:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.dataCadastro}"/></td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.biblioteca.descricao}"/></td>
			</tr>
			<tr>
				<th>Tipo do Documento:</th>
				<td>
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.tipoDocumento.denominacao}"/>
					<c:if test="${solicitacaoNormalizacaoMBean.obj.tipoDocumento.tipoDocumentoOutro}">
							(  ${solicitacaoNormalizacaoMBean.obj.outroTipoDocumento}  )
					</c:if>	
				</td>
			</tr>
			
			<tr>
				<th style="vertical-align:top;">Aspectos a Normalizar:</th>
				<td>
					<c:if test="${solicitacaoNormalizacaoMBean.obj.trabalhoTodo}">
						Trabalho no todo
						<br/>
					</c:if>
					 
					<c:if test="${!solicitacaoNormalizacaoMBean.obj.trabalhoTodo}">
					
						<c:if test="${solicitacaoNormalizacaoMBean.obj.referencias}">
							- Refer�ncias
							<br/>
						</c:if>
						
						<c:if test="${solicitacaoNormalizacaoMBean.obj.citacoes}">
							- Cita��es
							<br/>
						</c:if>
						
						<c:if test="${solicitacaoNormalizacaoMBean.obj.estruturaDoTrabalho}">
							- Estrutura do Trabalho
							<br/>
						</c:if>
						
						<c:if test="${solicitacaoNormalizacaoMBean.obj.preTextuais}">
							- Pr�-textuais
							<br/>
						</c:if>
						
						<c:if test="${solicitacaoNormalizacaoMBean.obj.proTextuais}">
							- Pr�-textuais
							<br/>
						</c:if>
					
					</c:if>
					 
					<c:if test="${solicitacaoNormalizacaoMBean.obj.outrosAspectosNormalizacao}">
						Outros aspectos da normaliza��o: <h:outputText value="#{solicitacaoNormalizacaoMBean.obj.descricaoOutrosAspectosNormalizacao}"/>
					</c:if> 
					
				</td>
			</tr>
			
			<%-- <tr>
				<th>Autoriza Descarte?</th>
				<td>
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.autorizaDescarte}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
				</td>
			</tr> --%>
		</tbody>
	</table>
	
	<div id="divCodigoVerificacao">
			C�digo de Autentica��o : ${solicitacaoNormalizacaoMBean.numeroAutenticacao}
	</div>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>