<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<h2>  <ufrn:subSistema /> > Confirma Remoção da Situação do Material</h2>

<div class="descricaoOperacao"> 
    <p>Caro Usuário, confirme a remoção da Situação do material, escolhendo a Situação para onde os materiais que possuem a Situação que está sendo removida serão migrados. </p>
</div>

<f:view>

	<a4j:keepAlive beanName="situacaoMaterialInformacionalMBean" />
	
	<h:form id="formConfirmaRemovaoStatus">
	
		<table class="formulario" width="70%">
			<caption>Confirme a Remoção da Situação</caption>
		
			<tbody>
		
				<tr>
					<th>Descrição:</th>
					<td>${situacaoMaterialInformacionalMBean.obj.descricao}</td>
					
					<th>Visível pelo Usuário:</th>
					<td style="width: 10%">
						<c:if test="${situacaoMaterialInformacionalMBean.obj.visivelPeloUsuario}">
							<span style="color: green;">SIM</span>
						</c:if>
						<c:if test="${! situacaoMaterialInformacionalMBean.obj.visivelPeloUsuario}">
							<span style="color: red;">NÃO </span>
						</c:if>
					</td>
		
				</tr>
				
				<tr>
					<th class="required">Nova Situação dos Materiais:</th>
					<td colspan="5">
						<h:selectOneMenu id="comboSituacoes" value="#{situacaoMaterialInformacionalMBean.novaSituacao.id}" >
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{situacaoMaterialInformacionalMBean.situacoesAtivas}"/>
						</h:selectOneMenu>
					</td>
				</tr>

			</tbody>
			<tfoot>
			
				<tr>
					<td colspan="6">
			
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
							<h:commandButton id="cmdBottonRemoveSituacao"  value="Remover Situação" action="#{situacaoMaterialInformacionalMBean.remover}" />
						</ufrn:checkRole>
						
						<h:commandButton id="cmdBottonCancelar" value="Cancelar" action="#{situacaoMaterialInformacionalMBean.listar}" immediate="true"  onclick="#{confirm}"/>
			
					</td>
				</tr>
			
			</tfoot>
		</table>

	</h:form>


	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>