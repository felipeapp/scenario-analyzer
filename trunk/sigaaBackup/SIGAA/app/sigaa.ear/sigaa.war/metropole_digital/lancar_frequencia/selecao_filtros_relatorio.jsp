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
	<a4j:keepAlive beanName="lancamentoFrequenciaIMD"/>
	<h2> <ufrn:subSistema /> > Relatório de Frequência por Turma > Seleção dos filtros</h2>
	
	<h:form id="form">
		<c:if test="${(not empty lancamentoFrequenciaIMD.turmaEntradaSelecionada)}">	
			<p align="center"><h2 align="center">TURMA: ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.anoReferencia}.${lancamentoFrequenciaIMD.turmaEntradaSelecionada.periodoReferencia} - ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.especializacao.descricao} - ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.cursoTecnico.nome}
			<br />OPÇÃO PÓLO GRUPO: ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.opcaoPoloGrupo.descricao}</h2></p>
		</c:if>
		
		<table class="formulario" style="width: 60%">
		  <caption>Se for necessário, informe os filtros para o relatório</caption>
	 		<tbody>
				<tr>
					<td colspan="5" >
						<h:inputHidden value="#{lancamentoFrequenciaIMD.turmaEntradaSelecionada.id}"/>
						<h:inputHidden value="#{lancamentoFrequenciaIMD.idTurmaEntradaSelecionada}"/>
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
						<t:inputCalendar value="#{lancamentoFrequenciaIMD.dataInicioInformada}"
							size="10" maxlength="10" readonly=""
							renderAsPopup="true" renderPopupButtonAsImage="true" id="dataInicio" popupDateFormat="dd/MM/yyyy"  onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" title="Data início" />
					</td>
					<td>
						até
					</td>
					<td>
						<t:inputCalendar value="#{lancamentoFrequenciaIMD.dataFimInformada}"
							size="10" maxlength="10" readonly=""
							renderAsPopup="true" renderPopupButtonAsImage="true" id="dataFim" popupDateFormat="dd/MM/yyyy"  onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" title="Data fim" />
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
						<h:inputText value="#{lancamentoFrequenciaIMD.qtdFaltasMinimaInformada}" size="10" maxlength="3" onkeypress="return mascara(this,masknumeros);" />
					</td>
					<td>
					 	até
				 	</td>
				 	<td>
						<h:inputText value="#{lancamentoFrequenciaIMD.qtdFaltasMaximaInformada}" size="10" maxlength="3" onkeypress="return mascara(this,masknumeros);" />
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
						<h:inputText value="#{lancamentoFrequenciaIMD.percFaltasMinimaInformada}" size="10" maxlength="3" onkeypress="return mascara(this,masknumeros);" />
					</td>
					<td>
						até
					</td>
					<td> 
						<h:inputText value="#{lancamentoFrequenciaIMD.percFaltasMaximaInformada}" size="10" maxlength="3" onkeypress="return mascara(this,masknumeros);"/>
					</td> 
				</tr>
				
		    </tbody>
		    <tfoot>
			    <tr>
					<td colspan="5">
						<h:commandButton value="Gerar Relatório com Filtros" action="#{lancamentoFrequenciaIMD.redirecionarRelatorioFrequenciaFiltrado}" id="listarComFiltros" />
						<h:commandButton value="Listar Todos" action="#{lancamentoFrequenciaIMD.redirecionarRelatorioFrequencia}" id="listarTodos" />
						<h:commandButton value="Cancelar" action="#{lancamentoFrequenciaIMD.cancelar}" onclick="#{confirm}" id="cancelar" />
					</td>
			    </tr>
			</tfoot>
		</table>
	</h:form>

</f:view>



<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>