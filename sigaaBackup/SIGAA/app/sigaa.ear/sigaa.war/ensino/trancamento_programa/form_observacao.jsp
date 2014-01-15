<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="trancamentoPrograma" />
	<h2> <ufrn:subSistema /> &gt; Solicita��es de Trancamento de Programa &gt; Indeferir Solicita��o</h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Usu�rio, </b>
		</p> 	
		<p>
			Voc� poder� escrever uma observa��o explicando o motivo pelo qual a Solicita��o de Trancamento de Programa foi indeferida.
		</p>
	</div>
	
	<h:form>
		<table class="formulario" width="70%">
			<caption>Informe o Motivo</caption>			
			<tr>
				<th style="width: 90px; vertical-align: top;">
					Motivo: <span class="obrigatorio">&nbsp;</span>
				</th>
				<td>
					<h:inputTextarea cols="80" rows="6" id="observacao" value="#{ trancamentoPrograma.obj.observacao}"/> 
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar" action="#{trancamentoPrograma.submeterIndeferimento}"/>
						<h:commandButton value="Cancelar" action="#{trancamentoPrograma.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>							
		</table>		
	</h:form>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
	</center>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	