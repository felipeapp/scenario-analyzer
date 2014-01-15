<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>	
	table.formulario td { padding-top:10px; padding-bottom:10px}
	table.formulario th { padding-top:10px; padding-bottom:10px}

.infoUsuario{
	width: 90%;
	margin-left: auto;
	margin-right:auto;
}

.infoUsuario caption{
	font-weight:bold;
	font-variant: small-caps;
}
	
.infoUsuario td{
	font-weight:bold;
}

.infoUsuario th{
	text-align: left;
}	
	
	
</style>


<h2> <ufrn:subSistema /> &gt; Emitir Histórico</h2>


<div class="descricaoOperacao">
	<p> <strong>OBSERVAÇÃO:</strong> Se o período não for fornecido todos os empréstimos serão recuperados.</p> 
</div>

<f:view>

	<h:form>
		
		<a4j:keepAlive beanName="emiteHistoricoEmprestimosMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean"></a4j:keepAlive>
		
		<%-- Exibe as informações do usuário. --%>
		<c:set var="_infoUsuarioCirculacao" value="${emiteHistoricoEmprestimosMBean.infoUsuarioBiblioteca}" scope="request"/>
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
		<br/>
		<table class="formulario" style="width:60%;">
				
			<caption style="margin-top:10px">Período dos Empréstimos</caption>
	
			<tr>
				<th style="width:100px;">Data Inicial:</th>
				<td style="width:150px;">
					<t:inputCalendar value="#{emiteHistoricoEmprestimosMBean.dataInicio}" 
							size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
							renderPopupButtonAsImage="true"
							onkeypress="return formataData(this, event)" />
				</td>
	
				<th style="width:100px;">Data Final:</th>
				<td>
					<t:inputCalendar value="#{emiteHistoricoEmprestimosMBean.dataFinal}" 
							size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
							renderPopupButtonAsImage="true"
							onkeypress="return formataData(this, event)" />
				</td>
	
			</tr>
			<tfoot>
				<tr>
					<td colspan="4" align="center">
						<h:commandButton value="Emitir Histórico" action="#{emiteHistoricoEmprestimosMBean.emiteHistorioEmprestimo}"/>
						
						<h:commandButton value="<< Voltar"action="#{emiteHistoricoEmprestimosMBean.voltarTelaBusca}" rendered="#{! emiteHistoricoEmprestimosMBean.emitirProprioHistorio}"/>
						
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{emiteHistoricoEmprestimosMBean.cancelar}" immediate="true" />
						
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>