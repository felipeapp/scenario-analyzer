<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="planoDocenciaAssistidaMBean" />
	<h2> <ufrn:subSistema /> &gt; Plano de Doc�ncia Assistida</h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Usu�rio, </b>
		</p> 	
		<p>
			Voc� dever� informar a nova Situa��o e escrever uma Observa��o, informando o motivo pelo qual est� sendo alterado.
		</p>
	</div>
	
	<table class="visualizacao" style="width: 90%">
		<caption>Dados do Aluno</caption>
		<tr>
			<th>Nome:</th>
			<td>${planoDocenciaAssistidaMBean.obj.discente.matriculaNome}</td>		
		</tr>
		<tr>
			<th>Programa:</th>
			<td>${planoDocenciaAssistidaMBean.obj.discente.unidade.nome}</td>		
		</tr>	
		<tr>
			<th>Orientador:</th>
			<td>${planoDocenciaAssistidaMBean.orientacao.descricaoOrientador}</td>		
		</tr>		
		<tr>
			<th>N�vel:</th>
			<td>${planoDocenciaAssistidaMBean.obj.discente.nivelDesc}</td>		
		</tr>
		<tr>
			<th>Ano/Per�odo:</th>
			<td>${planoDocenciaAssistidaMBean.obj.ano}.${planoDocenciaAssistidaMBean.obj.periodo}</td>		
		</tr>
		<tr>
			<th>Situa��o Atual:</th>
			<td>${planoDocenciaAssistidaMBean.obj.descricaoStatus}</td>		
		</tr>						
	</table>	
	
	<br/>
	
	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Informe os dados da An�lise</caption>		
			<tr>
				<th class="obrigatorio">Situa��o:</th>
				<td>
					<h:selectOneMenu id="situacao" value="#{planoDocenciaAssistidaMBean.novoStatus}">
						<f:selectItem itemLabel="-- Selecione a Situa��o --" itemValue="0"/>
						<f:selectItems value="#{planoDocenciaAssistidaMBean.statusAnalisePlanoCombo}"/>
					</h:selectOneMenu>				
				</td>			
			</tr>	
			<tr>
				<th class="obrigatorio">
					Observa��o:
				</th>
				<td>
					<h:inputTextarea cols="80" rows="6" id="observacao" value="#{ planoDocenciaAssistidaMBean.obj.observacao}"/> 
				</td>
			</tr>		
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar" action="#{planoDocenciaAssistidaMBean.cadastrar}" id="botaoDeconfirmacao"/>
						<h:commandButton value="Cancelar" action="#{planoDocenciaAssistidaMBean.cancelarGeral}" onclick="#{confirm}" immediate="true" id="cancelamento"/>
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
	