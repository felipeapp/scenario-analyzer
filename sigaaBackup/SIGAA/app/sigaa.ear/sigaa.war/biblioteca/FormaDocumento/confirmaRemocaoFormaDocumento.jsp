<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<h2>  <ufrn:subSistema /> > Confirma Remo��o da Forma do Documento </h2>

<div class="descricaoOperacao"> 
    <p>
    	Caro Usu�rio, confirme a remo��o da Forma do Documento selecionado, escolhendo a forma para onde os materiais que possuem a forma que est� sendo removida ser�o migrados. 
    </p>
    <p>Como o forma do documento � opcional, caso n�o se escolha nenhuma nova forma, os materiais apenas deixaram de possuir a forma removida.
    </p>
</div>

<f:view>

	<a4j:keepAlive beanName="formaDocumentoMBean" />
	
	<h:form id="formConfirmaRemovaoTipomaterial">
	
		<table class="formulario" width="70%">
			<caption>Confirme a Remo��o da Forma do Documento</caption>
		
			<tbody>
		
				<tr>
					<th style="width: 50%">Descri��o:</th>
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
							<h:commandButton id="cmdBottonRemoveStatus"  value="Remover Forma do Documento" action="#{formaDocumentoMBean.remover}" onclick="return confirm('Confirma a remo��o da Forma do Documento ? ');" />
						</ufrn:checkRole>
						
						<h:commandButton id="cmdBottonCancelar" value="Cancelar" action="#{formaDocumentoMBean.listar}" immediate="true"  onclick="#{confirm}"/>
			
					</td>
				</tr>
			
			</tfoot>
		</table>

	</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>