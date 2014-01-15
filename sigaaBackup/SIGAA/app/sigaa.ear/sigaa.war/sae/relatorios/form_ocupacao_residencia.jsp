<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema/> > Relat�rio de Ocupa��o de Resid�ncias</h2>

	<h:form id="form">
		<table class="formulario" style="width: 50%">
			<caption>Informe os crit�rios para a emiss�o do relat�rio</caption>

			<tr>
				<th class="required" width="30%">Ano.Per�odo:</th>
				<td>
					<h:inputText id="ano" value="#{relatoriosSaeMBean.ano}" 
					size="4" maxlength="4" onkeyup="formatarInteiro(this)" />
					.<h:inputText id="semestre" value="#{relatoriosSaeMBean.periodo}" size="1"
					maxlength="1" onkeyup="formatarInteiro(this)" />
				</td>
			</tr>
			
			<tr>
				<th class="required">Resid�ncia:</th>
				<td>
					<h:selectOneMenu id="centro" value="#{relatoriosSaeMBean.residencia.id}">
						<f:selectItem itemValue="0" itemLabel="-- Selecione --"  />
						<f:selectItems value="#{residenciaUniversitariaMBean.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>	
					<td colspan="2">
						<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioOcupacaoResidencia}" value="Emitir Relat�rio" />
						<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" immediate="true" 
						 	onclick="return confirm('Deseja cancelar a opera��o? Todos os dados digitados n�o salvos ser�o perdidos!');"> 
						</h:commandButton>
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<div align="center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>