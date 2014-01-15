<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema/> > Relat�rio de Residentes no Cadastro �nico</h2>

	<h:form id="form">
		<table class="formulario" style="width: 60%">
			<caption>Informe os crit�rios para a emiss�o do relat�rio</caption>

			<tr>
				<th class="required">Ano-Per�odo:</th>
				<td>
					<h:inputText id="ano" 
						value="#{relatoriosSaeMBean.ano}"
						size="5" 
						maxlength="4" 
						onclick="formatarInteiro(this)" /> - 
					
					<h:inputText id="semestre" 
								value="#{relatoriosSaeMBean.periodo}" 
								size="2" 
								maxlength="1" 
								onclick="formatarInteiro(this)" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioResidentesNoCadastroUnico}" value="Emitir Relat�rio" />
						<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>