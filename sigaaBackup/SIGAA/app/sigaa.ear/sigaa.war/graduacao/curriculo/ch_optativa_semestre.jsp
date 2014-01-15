<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h:form>

<h2><ufrn:subSistema /> &gt; Estrutura Curricular de Matrizes Curriculares &gt; CH Optativa por Período</h2>

<div class="descricaoOperacao" style="width:70%">
	<h4> Caro usuário, </h4>	
	<p>
		Neste formulário você poderá informar as carga horárias de componentes optativos do currículo 
		que um discente deve pagar por período letivo. 
	</p>
	<p>
		Estes dados serão utilizados na construção do relatório de integralização do currículo.
	</p>
</div>

<table class="formulario" width="65%">
	<caption>Cargas Horárias optativas por nível</caption>
	<tr>
		<td colspan="2" style="background: #DEDEDE; text-align: center; padding: 10px;"> 
			Carga Horária Optativa do Currículo:<b> ${curriculo.obj.chOptativasMinima} horas
		</th>
	</tr>
	<c:forEach items="#{ curriculo.chOptativaSemestre }" var="cos" varStatus="i">
		<tr class="${ i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<th align="right" style="font-variant: small-caps; font-size: 1.1em; ">
				<h:outputText value="#{ cos.semestre }"/>º Nível: 
			</th>
			<td align="center">
				<h:inputText value="#{ cos.ch }" size="5" onkeyup="return formatarInteiro(this);" style="text-align: right; padding: 2px;"/> <small>horas</small>
			</td>
		</tr>
	</c:forEach>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton id="voltar" action="#{curriculo.voltarDadosGerais}" value="<< Dados Gerais" />
			<h:commandButton value="<< Componentes" action="#{curriculo.voltarComponentes}" /> 
			<h:commandButton value="<< TCC Definitivo" action="#{curriculo.telaTccDefinitivo}" rendered="#{curriculo.graduacao}"/> 
			<h:commandButton id="cancelar" action="#{curriculo.cancelar}" value="Cancelar" onclick="#{confirm}" />
			<h:commandButton action="#{ curriculo.submeterChOptativaSemestre }" value="Próximo Passo >>"/>
		</td>
	</tr>
</tfoot>
</table>


</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
