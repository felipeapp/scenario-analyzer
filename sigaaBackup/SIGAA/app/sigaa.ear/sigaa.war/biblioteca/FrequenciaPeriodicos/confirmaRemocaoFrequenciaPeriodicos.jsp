<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<h2>  <ufrn:subSistema /> > Confirma Remoção da Periodicidade </h2>

<div class="descricaoOperacao"> 
    <p>Caro Usuário, </p>
    <p> Confirme a remoção da <i>Periodicidade</i> selecionada, escolhendo a nova periodicidade para a qual 
    as assinaturas que possuem a periodicidade que está sendo removida serão migradas.  </p>
</div>

<f:view>

	<a4j:keepAlive beanName="frequenciaPeriodicosMBean" />
	
	<h:form id="formConfirmaRemovaoFrequenciaPeriodicos">
	
		<table class="formulario" width="70%">
			<caption>Confirme a Remoção da Periodicidade</caption>
		
			<tbody>
		
				<tr>
					<th style="font-weight: bold;">Descrição:</th>
					<td>${frequenciaPeriodicosMBean.obj.descricaoCompleta}</td>
		
				</tr>
				
				<tr>
					<th class="required">Nova Periodicidade para as Assinaturas:</th>
					<td colspan="5">
						<h:selectOneMenu id="comboFrequencia" value="#{frequenciaPeriodicosMBean.frequenciaParaMigraAssinaturas.id}" >
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{frequenciaPeriodicosMBean.frequenciasAtivasComboBox}"/>
						</h:selectOneMenu>
					</td>
				</tr>

			</tbody>
			<tfoot>
			
				<tr>
					<td colspan="6">
			
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO }  %>">
							<h:commandButton id="cmdBottonRemoveFrequencia"  value="Remover" action="#{frequenciaPeriodicosMBean.remover}" />
						</ufrn:checkRole>
						
						<h:commandButton id="cmdBottonCancelar" value="Cancelar" action="#{frequenciaPeriodicosMBean.listar}" immediate="true"  onclick="#{confirm}"/>
			
					</td>
				</tr>
			
			</tfoot>
		</table>

	</h:form>


	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>