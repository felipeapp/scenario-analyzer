<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<h2>  <ufrn:subSistema /> > Confirma Remoção da Coleção </h2>

<div class="descricaoOperacao"> 
    <p>Caro Usuário, confirme a remoção da Coleção, escolhendo a coleçaão para onde os materiais que possuem a coleção que está sendo removido serão migrados. </p>
</div>

<f:view>

	<a4j:keepAlive beanName="colecaoMBean" />
	
	<h:form id="formConfirmaRemovaoColecao">
	
		<table class="formulario" width="70%">
			<caption>Confirme a Remoção da Coleção</caption>
		
			<tbody>
		
				<tr>
					<th>Descrição:</th>
					<td>${colecaoMBean.obj.descricao}</td>
		
				</tr>
				
				<tr>
					<th class="required">Nova Coleção para os Materiais:</th>
					<td colspan="5">
						<h:selectOneMenu id="comboColecoes" value="#{colecaoMBean.novaColecao.id}" >
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{colecaoMBean.colecoesAtivas}"/>
						</h:selectOneMenu>
					</td>
				</tr>

			</tbody>
			<tfoot>
			
				<tr>
					<td colspan="6">
			
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
							<h:commandButton id="cmdBottonRemoveColecao"  value="Remover Coleção" action="#{colecaoMBean.remover}" />
						</ufrn:checkRole>
						
						<h:commandButton id="cmdBottonCancelar" value="Cancelar" action="#{colecaoMBean.listar}" immediate="true"  onclick="#{confirm}"/>
			
					</td>
				</tr>
			
			</tfoot>
		</table>

	</h:form>


	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>