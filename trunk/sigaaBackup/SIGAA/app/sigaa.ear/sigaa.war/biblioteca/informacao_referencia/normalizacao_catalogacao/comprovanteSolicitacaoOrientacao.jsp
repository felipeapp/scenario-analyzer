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

	<h2>Comprovante de solicitação de orientação de normalização</h2>

	<table class="tabelaRelatorio" width="100%">
	
		<tbody>
		
			<tr>
				<th width="40%">Número da Solicitação:</th>
				<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.numeroSolicitacao}"/></td>
			</tr>
		
			<c:if test="${not empty solicitacaoOrientacaoMBean.obj.discente}">
				
				<tr>
					<th width="40%">Solicitante:</th>
					<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.discente}"/></td>
				</tr>
				
				<th>Categoria:</th>
				
				<c:if test="${  ( solicitacaoOrientacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						|| solicitacaoOrientacaoMBean.obj.discente.lato 
						|| solicitacaoOrientacaoMBean.obj.discente.mestrado 
						|| solicitacaoOrientacaoMBean.obj.discente.doutorado}">
					
					<td>Aluno de Pós-Graduação</td>
				</c:if>
				<c:if test="${ ! ( solicitacaoOrientacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						&& !solicitacaoOrientacaoMBean.obj.discente.lato 
						&& !solicitacaoOrientacaoMBean.obj.discente.mestrado 
						&& !solicitacaoOrientacaoMBean.obj.discente.doutorado}">
					<td>Aluno de Graduação</td>
				</c:if>
				
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.discente.curso}"/></td>
				</tr>
				
			</c:if>
			
			<c:if test="${not empty solicitacaoOrientacaoMBean.obj.servidor}">
				<tr>
					<th>Solicitante:</th>
					<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.servidor}"/></td>
				</tr>
				<tr>
					<th>Categoria:</th>
					<td>
						<c:if test="${solicitacaoOrientacaoMBean.obj.servidor.categoria.docente}" >
							Docente
						</c:if>
						<c:if test="${solicitacaoOrientacaoMBean.obj.servidor.categoria.tecnico}" >
							Técnico Administrativo
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Lotação:</th>
					<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.servidor.unidade}"/></td>
				</tr>
			</c:if>
		
			<tr>
				<th>Telefone:</th>
				<td> ${solicitacaoOrientacaoMBean.obj.pessoa.telefone} </td>
			</tr>
			
			<tr>
				<th>Celular:</th>
				<td> ${solicitacaoOrientacaoMBean.obj.pessoa.celular} </td>
			</tr>
			
			<tr>
				<th>Email:</th>
				<td> ${solicitacaoOrientacaoMBean.obj.pessoa.email} </td>
			</tr>
			
			<tr>
				<th>Data da Solicitação:</th>
				<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.dataCadastro}"/></td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.biblioteca.descricao}"/></td>
			</tr>
		
			<tr>
				<th>Comentários do solicitante:</th>
				<td>
					<h:outputText value="#{solicitacaoOrientacaoMBean.obj.comentariosUsuario}" />
				</td>
			</tr>
			
			<c:if test="${solicitacaoOrientacaoMBean.obj.atendido || solicitacaoOrientacaoMBean.obj.confirmado || solicitacaoOrientacaoMBean.obj.cancelado}">
				<tr>
					<th>Comentários do bibliotecário:</th>
					<td>
						<h:outputText value="#{solicitacaoOrientacaoMBean.obj.comentariosBibliotecario}" />
					</td>
				</tr>
				
				<tr>
					<th>Data/Horário definido:</th>
					<td>
						<h:outputText value="#{solicitacaoOrientacaoMBean.obj.descricaoHorarioAtendimento}" />
					</td>
				</tr>
			</c:if>
		</tbody>
	</table>
	
	<div id="divCodigoVerificacao">
			Código de Autenticação : ${solicitacaoOrientacaoMBean.numeroAutenticacao}
	</div>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>