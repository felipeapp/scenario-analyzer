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

	<h2>Comprovante de solicita��o de orienta��o de normaliza��o</h2>

	<table class="tabelaRelatorio" width="100%">
	
		<tbody>
		
			<tr>
				<th width="40%">N�mero da Solicita��o:</th>
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
					
					<td>Aluno de P�s-Gradua��o</td>
				</c:if>
				<c:if test="${ ! ( solicitacaoOrientacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						&& !solicitacaoOrientacaoMBean.obj.discente.lato 
						&& !solicitacaoOrientacaoMBean.obj.discente.mestrado 
						&& !solicitacaoOrientacaoMBean.obj.discente.doutorado}">
					<td>Aluno de Gradua��o</td>
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
							T�cnico Administrativo
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Lota��o:</th>
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
				<th>Data da Solicita��o:</th>
				<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.dataCadastro}"/></td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.biblioteca.descricao}"/></td>
			</tr>
		
			<tr>
				<th>Coment�rios do solicitante:</th>
				<td>
					<h:outputText value="#{solicitacaoOrientacaoMBean.obj.comentariosUsuario}" />
				</td>
			</tr>
			
			<c:if test="${solicitacaoOrientacaoMBean.obj.atendido || solicitacaoOrientacaoMBean.obj.confirmado || solicitacaoOrientacaoMBean.obj.cancelado}">
				<tr>
					<th>Coment�rios do bibliotec�rio:</th>
					<td>
						<h:outputText value="#{solicitacaoOrientacaoMBean.obj.comentariosBibliotecario}" />
					</td>
				</tr>
				
				<tr>
					<th>Data/Hor�rio definido:</th>
					<td>
						<h:outputText value="#{solicitacaoOrientacaoMBean.obj.descricaoHorarioAtendimento}" />
					</td>
				</tr>
			</c:if>
		</tbody>
	</table>
	
	<div id="divCodigoVerificacao">
			C�digo de Autentica��o : ${solicitacaoOrientacaoMBean.numeroAutenticacao}
	</div>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>