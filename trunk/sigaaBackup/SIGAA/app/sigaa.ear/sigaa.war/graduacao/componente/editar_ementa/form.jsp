<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/stricto/menu_coordenador.jsp" %>

<h2 class="title"><ufrn:subSistema/> &gt; Ementas e Referências de Componentes Curriculares</h2>

<h:form id="form">
	<table class="visualizacao" style="width:80%">
		<caption>Dados Gerais do Componente Curricular</caption>
		<tr>
			<th width="30%">Código:</th>
			<td><h:outputText value="#{ementaComponenteCurricularMBean.obj.codigo}" /></td>
		</tr>
		<tr>
			<th>Nome:</th>
			<td><h:outputText value="#{ementaComponenteCurricularMBean.obj.detalhes.nome}" /></td>
		</tr>
		<tr>
			<th>Carga Horária Total:</th>
			<td>
			<h:outputText value="#{ementaComponenteCurricularMBean.obj.chTotal}" /> h.</td>
		</tr>
		<tr>
			<th>Unidade Responsável:</th>
			<td><h:outputText value="#{ementaComponenteCurricularMBean.obj.unidade.nome}" /></td>
		</tr>
		<c:if test="${not empty ementaComponenteCurricularMBean.obj.curso.id and ementaComponenteCurricularMBean.obj.curso.id > 0}">
			<tr>
				<th>Curso:</th>
				<td><h:outputText value="#{ementaComponenteCurricularMBean.obj.curso.descricao}" /></td>
			</tr>
		</c:if>
		<c:if test="${ementaComponenteCurricularMBean.obj.curso.id == 0 and ementaComponenteCurricularMBean.obj.cursoNovo != null}">
			<tr>
				<th>Curso:</th>
				<td><h:outputText value="#{ementaComponenteCurricularMBean.obj.cursoNovo}" /> (Curso Novo)</td>
			</tr>
		</c:if>
		<tr>
			<th>Tipo do Componente Curricular: </th>
			<td><h:outputText value="#{ementaComponenteCurricularMBean.obj.tipoComponente.descricao}" /></td>
		</tr>
		<c:if test="${ ementaComponenteCurricularMBean.obj.passivelTipoAtividade }">
			<tr>
				<th>Tipo de ${ementaComponenteCurricularMBean.obj.atividade ? 'Atividade' : 'Disciplina'}:</th>
				<td><h:outputText value="#{ementaComponenteCurricularMBean.obj.tipoAtividade.descricao}" /></td>
			</tr>
			<tr>
				<th>Forma de Participação:</th>
				<td><h:outputText value="#{ementaComponenteCurricularMBean.obj.formaParticipacao.descricao}" /></td>
			</tr>				
		</c:if>
	</table>
	
	<table class="formulario" style="width:80%">
	<caption>Informe os Dados Complementares</caption>
	<tbody>
		<c:if test="${!ementaComponenteCurricularMBean.obj.bloco}">
			<tr>
				<th valign="top" id="campoEmenta" class="required">
					<c:if test="${ementaComponenteCurricularMBean.obj.atividade}">
						Descrição:
					</c:if>
					<c:if test="${not ementaComponenteCurricularMBean.obj.atividade}">
						Ementa:
					</c:if>
				</th>
				<td>
					<h:inputTextarea id="ementa"
						value="#{ementaComponenteCurricularMBean.obj.detalhes.ementa}" cols="85" rows="4" />
				</td>
			</tr>	
		</c:if>
		<tr>
			<th valign="top" id="campoReferencia">Referências Bibliográficas:</th>
			<td>
				<h:inputTextarea id="bibliografia"
					value="#{ementaComponenteCurricularMBean.obj.bibliografia}"
					cols="85" rows="4" />
			</td>
		</tr>		
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{ementaComponenteCurricularMBean.cancelar}" immediate="true" />
				<h:commandButton id="cadastrar" value="Cadastrar" action="#{ementaComponenteCurricularMBean.cadastrar}" />
			</td>
		</tr>
	</tfoot>
	</table>
	<br/>
	<c:set var="exibirApenasSenha" value="true" scope="request"/>
	<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
	<br>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
