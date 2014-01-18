<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
function mascara(o,f){
    obj = o
    fun = f
    setTimeout("gerarmascara()",1)
}

function gerarmascara(){
    obj.value=fun(obj.value)
}

function masknumeros(texto){
    texto = texto.replace(/\D/g,"")
    return texto
}
</script>

<f:view>
	<a4j:keepAlive beanName="relatorioFrequenciaAssitenteSocial"/>
	<h2> <ufrn:subSistema /> > Relatório de Frequência</h2>
	
	<h:form id="form">
		
		
		<c:if test="${not empty relatorioFrequenciaAssitenteSocial.tutoriasSelecionadas}">
			<table class=listagem style="width:90%">
				<caption class="listagem">Turmas Selecionadas</caption>
				<thead> 
					<tr>
						<td>Turma</td>
						<td>Tutor</td>
						<td>Módulo</td>
						<td>Opção - Pólo - Grupo</td>
						<td>Ano - Período</td>
						<td>Horário</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{relatorioFrequenciaAssitenteSocial.tutoriasSelecionadas}" var="tutoria" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${tutoria.turmaEntrada.especializacao.descricao}</td>
							<td>${tutoria.tutor.pessoa.nome}</td>
							<td>${tutoria.turmaEntrada.dadosTurmaIMD.cronograma.modulo.descricao}</td>
							<td>${tutoria.turmaEntrada.opcaoPoloGrupo.descricao}</td>
							<td>${tutoria.turmaEntrada.anoReferencia}.${tutoria.turmaEntrada.periodoReferencia}</td>
							<td>${tutoria.turmaEntrada.dadosTurmaIMD.horario}</td>
						</tr>		
					</c:forEach>
				</tbody>
				
		 	</table>
		 	<br />
		 	<br />
		 	<br />
	 	</c:if>
		
		<table class="formulario" style="width: 60%">
		  <caption>Filtros do relatório</caption>
	 		<tbody>
				<tr>
					<td colspan="5" >
						<h:inputHidden value="#{relatorioFrequenciaAssitenteSocial.idOpcaoPoloSelecionado}"/>
					</td>
				</tr>
				
				
				<tr>
					<th>
						Período
					</th>
					<td>
						de
					</td>
					<td style="width: 100px;"> 
						<t:inputCalendar value="#{relatorioFrequenciaAssitenteSocial.dataInicioInformada}"
							size="10" maxlength="10" readonly=""
							renderAsPopup="true" renderPopupButtonAsImage="true" id="dataInicio" popupDateFormat="dd/MM/yyyy"  onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" title="Data início" />
					</td>
					<td>
						até
					</td>
					<td>
						<t:inputCalendar value="#{relatorioFrequenciaAssitenteSocial.dataFimInformada}"
							size="10" maxlength="10" readonly=""
							renderAsPopup="true" renderPopupButtonAsImage="true" id="dataFim" popupDateFormat="dd/MM/yyyy"  onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" title="Data fim" />
						<ufrn:help>Informe pelo menos a data início ou a data fim para gerar o relatório com filtros. </ufrn:help>
					</td>
					
				</tr>
			
				
				<tr>
					<th>
						Quantidade de faltas
					</th>
					<td>
						de
					</td>
					<td> 
						<h:inputText value="#{relatorioFrequenciaAssitenteSocial.qtdFaltasMinimaInformada}" size="10" maxlength="3" onkeypress="return mascara(this,masknumeros);" />
					</td>
					<td>
					 	até
				 	</td>
				 	<td>
						<h:inputText value="#{relatorioFrequenciaAssitenteSocial.qtdFaltasMaximaInformada}" size="10" maxlength="3" onkeypress="return mascara(this,masknumeros);" />
					</td> 
				</tr>
		
				<tr>
					<th>
						Percentual de faltas
					</th>
					<td>
						de
					</td>
					<td>
						<h:inputText value="#{relatorioFrequenciaAssitenteSocial.percFaltasMinimaInformada}" size="10" maxlength="3" onkeypress="return mascara(this,masknumeros);" />%
					</td>
					<td>
						até
					</td>
					<td> 
						<h:inputText value="#{relatorioFrequenciaAssitenteSocial.percFaltasMaximaInformada}" size="10" maxlength="3" onkeypress="return mascara(this,masknumeros);"/>%
					</td> 
				</tr>
				
		    </tbody>
		    <tfoot>
			    <tr>
					<td colspan="5">
						<h:commandButton value="Gerar Relatório com Filtros" action="#{relatorioFrequenciaAssitenteSocial.redirecionarRelatorioFrequenciaFiltrado}" id="listarComFiltros" />
						<h:commandButton value="<< Voltar" action="#{relatorioFrequenciaAssitenteSocial.voltarForm}" />
						<h:commandButton value="Cancelar" action="#{relatorioFrequenciaAssitenteSocial.cancelarFiltros}"  onclick="#{confirm}" id="cancelar" />
					</td>
			    </tr>
			</tfoot>
		</table>
	</h:form>

</f:view>



<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>