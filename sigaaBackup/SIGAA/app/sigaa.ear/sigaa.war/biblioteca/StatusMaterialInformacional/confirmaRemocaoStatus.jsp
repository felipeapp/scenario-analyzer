<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<h2>  <ufrn:subSistema /> > Confirma Remoção do Status do Material</h2>

<div class="descricaoOperacao"> 
    <p>Caro Usuário, confirme a remoção do status do material, escolhendo o status para onde os materiais que possuem o status que está sendo removido serão migrados. </p>
</div>

<f:view>

	<a4j:keepAlive beanName="statusMaterialInformacionalMBean" />
	
	<h:form id="formConfirmaRemovaoStatus">
	
		<table class="formulario" width="70%">
			<caption>Confirme a Remoção do Status</caption>
		
			<tbody>
		
				<tr>
					<th>Descrição:</th>
					<td>${statusMaterialInformacionalMBean.obj.descricao}</td>
					
					<th>Permite Emprestimos:</th>
					<td style="width: 10%">
						<c:if test="${statusMaterialInformacionalMBean.obj.permiteEmprestimo}">
							<span style="color: green;">SIM</span>
						</c:if>
						<c:if test="${! statusMaterialInformacionalMBean.obj.permiteEmprestimo}">
							<span style="color: red;">NÃO </span>
						</c:if>
					</td>
					
					<th>Aceita Reserva:</th>
					<td style="width: 10%">
						<c:if test="${statusMaterialInformacionalMBean.obj.aceitaReserva}">
							<span style="color: green;">SIM</span>
						</c:if>
						<c:if test="${! statusMaterialInformacionalMBean.obj.aceitaReserva}">
							<span style="color: red;">NÃO </span>
						</c:if>
					</td>
		
				</tr>
				
				<tr>
					<th class="required">Novo Status dos Materiais:</th>
					<td colspan="5">
						<h:selectOneMenu id="comboStatus" value="#{statusMaterialInformacionalMBean.novoStatus.id}" >
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{statusMaterialInformacionalMBean.statusAtivos}"/>
						</h:selectOneMenu>
					</td>
				</tr>

			</tbody>
			<tfoot>
			
				<tr>
					<td colspan="6">
			
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
							<h:commandButton id="cmdBottonRemoveStatus"  value="Remover Status" action="#{statusMaterialInformacionalMBean.remover}" />
						</ufrn:checkRole>
						
						<h:commandButton id="cmdBottonCancelar" value="Cancelar" action="#{statusMaterialInformacionalMBean.listar}" immediate="true"  onclick="#{confirm}"/>
			
					</td>
				</tr>
			
			</tfoot>
		</table>

	</h:form>


	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>