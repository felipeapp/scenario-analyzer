<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<h2>  <ufrn:subSistema /> > Confirma Remoção da Forma do Documento </h2>

<div class="descricaoOperacao"> 
    <p>
    	Caro Usuário, confirme a remoção da Forma do Documento selecionado, escolhendo a forma para onde os materiais que possuem a forma que está sendo removida serão migrados. 
    </p>
    <p>Como o forma do documento é opcional, caso não se escolha nenhuma nova forma, os materiais apenas deixaram de possuir a forma removida.
    </p>
</div>

<f:view>

	<a4j:keepAlive beanName="formaDocumentoMBean" />
	
	<h:form id="formConfirmaRemovaoTipomaterial">
	
		<table class="formulario" width="70%">
			<caption>Confirme a Remoção da Forma do Documento</caption>
		
			<tbody>
		
				<tr>
					<th style="width: 50%">Descrição:</th>
					<td style="width: 50%">${formaDocumentoMBean.obj.denominacao}</td>
		
				</tr>
				
				<tr>
					<th>Nova Forma do Documento para os Materiais:</th>
					<td colspan="5">
						<h:selectOneMenu id="comboStatus" value="#{formaDocumentoMBean.novaFormaDocumento.id}" >
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{formaDocumentoMBean.formaDocumentoAtivas}"/>
						</h:selectOneMenu>
					</td>
				</tr>

			</tbody>
			<tfoot>
			
				<tr>
					<td colspan="6">
			
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
							<h:commandButton id="cmdBottonRemoveStatus"  value="Remover Forma do Documento" action="#{formaDocumentoMBean.remover}" onclick="return confirm('Confirma a remoção da Forma do Documento ? ');" />
						</ufrn:checkRole>
						
						<h:commandButton id="cmdBottonCancelar" value="Cancelar" action="#{formaDocumentoMBean.listar}" immediate="true"  onclick="#{confirm}"/>
			
					</td>
				</tr>
			
			</tfoot>
		</table>

	</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>