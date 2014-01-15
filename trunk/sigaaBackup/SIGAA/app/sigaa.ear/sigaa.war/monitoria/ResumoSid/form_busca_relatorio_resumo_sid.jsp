<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="tituloPagina"><ufrn:subSistema /> > Relatório de Resumos SID</h2>
	<h:form>
		<table class="formulario" width="35%" align="center">
			<caption>Escolha o ano do relatório</caption>
			<tr>
				<th width="30%" align="right" class="obrigatorio"> Ano: </th>
				<td width="50%" align="left">
					<h:inputText maxlength="4" value="#{resumoSid.buscaAnoSid}" onkeyup="return formatarInteiro(this)" size="4"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Gerar Relatório" action="#{resumoSid.emitirRelatorioResumoSid}" />
						<h:commandButton value="Cancelar" action="#{resumoSid.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		 <br />
		 <center>
			 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
			 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
		 </center>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>