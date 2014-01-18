<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>

<script>
function mudacor(ref,cor){
  ref.style.backgroundColor=cor;
}

function validaValorFrequencia(ref){ 
	avisado = false
	freqValida = true 
   
   if (ref.value != 1 && ref.value != "1,0" && ref.value != 0 && ref.value != "0,0" && ref.value != "0,5" && ref.value != null) {
	   freqValida = false 
   }
   
   if (!freqValida){ 
      if (!avisado){ 
         //se nao � valido, aviso 
         alert ("O valor digitado para a frequ\u00eancia \u00E9 inv\u00e1lido.") 
         //seleciono o texto 
         ref.select() 
         //coloco outra vez o foco 
         ref.focus() 
         avisado = true 
         setTimeout('avisado=false',50) 
      } 
   }	
} 
</script>

<style>
	tr.selecionada td { background: #C4D2EB; }
</style>

<h2><ufrn:subSistema /> > Lan�ar frequ�ncia da turma IMD </h2>


<a4j:keepAlive beanName="lancamentoFrequenciaIMD"/>
<f:view>
	<h:form>
		
		
		<c:if test="${(not empty lancamentoFrequenciaIMD.listaDiscentesTurma) && (not empty lancamentoFrequenciaIMD.listaPeriodosTurma) && (not empty lancamentoFrequenciaIMD.tabelaAcompanhamento) }">
			
			<div class="descricaoOperacao">
				<p>Atrav�s deste recurso � poss�vel registrar a frequ�ncia dos alunos desta turma de acordo com o per�odo de avalia��o do encontro presencial.</p>
				<p><b>Para indicar a frequ�ncia de um aluno, informe os seguintes n�meros: </b></p>
				<p><b><font style="color:red;">1</font>     = Presente</b></p>
				<p><b><font style="color:red;">0</font>     = Falta</b></p>
				<p><b><font style="color:red;">0.5</font>   = Meia falta / meia presen�a</b></p>
				<p><b><font style="color:red;">VAZIO</font> = Frequ�ncia n�o informada</b></p>
				
			</div>
			
			<p align="center"><h2 align="center">TURMA: ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.anoReferencia}.${lancamentoFrequenciaIMD.turmaEntradaSelecionada.periodoReferencia} - ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.especializacao.descricao} - ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.cursoTecnico.nome}
			<br />OP��O P�LO GRUPO: ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.opcaoPoloGrupo.descricao}</h2></p>
				
			
			<table class="formulario" style="width: auto">

			  	<tbody>
				  	<tr onfocus="mudacor(this,'#C4D2EB');" onblur="mudacor(this,'white');">
				  		<td>
					  		<rich:dataTable value="#{lancamentoFrequenciaIMD.tabelaAcompanhamento}" var="tab" id="tabela" rowKeyVar="r" styleClass="listagem" headerClass="" rowClasses="linhaPar, linhaImpar" onRowMouseOver="$(this).addClassName('selecionada')"
 onRowMouseOut="$(this).removeClassName('selecionada')" >
						  		<rich:column styleClass=""  style="width: 80px;" >
									<f:facet name="header">
					                  	<h:outputText value="MATR�CULA" />
					              	</f:facet>
					            	<h:outputText value="#{lancamentoFrequenciaIMD.listaDiscentesTurma[r].discente.matricula}"/>
								</rich:column>
						
								<rich:column style="width: 200px;">
									<f:facet name="header">	
					                  	<h:outputText value="DISCENTE" />
				              		</f:facet>
					              	<h:outputText value="#{lancamentoFrequenciaIMD.listaDiscentesTurma[r].discente.pessoa.nome}" />
								</rich:column>
						
								
								<rich:columns value="#{lancamentoFrequenciaIMD.listaPeriodosTurma}" var="colunas" index="c" colspan="2" id="colunas" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}">
									<f:facet name="header">
					                  	<f:verbatim>										
					                  		<acronym title="PER�ODO ${colunas.numeroPeriodo}: DE ${colunas.diaMesInicioTexto} A ${colunas.diaMesFimTexto} (${colunas.chTotalPeriodo}h)"  style="text-align:center; cursor:pointer;border:none;">
					                  			P<fmt:formatNumber value="${colunas.numeroPeriodo}" pattern="00"/>
					                  			</acronym>
					                  			<br />	
					                  	</f:verbatim>					                  	
					              	</f:facet>
					              	<c:if test="${lancamentoFrequenciaIMD.dataAtual >= colunas.dataInicio}">
			              				<h:inputText value="#{tab[c].frequencia}" size="1" maxlength="3" tabindex="#{c}" onblur="validaValorFrequencia(this);" onkeydown="return(formataValorNota(this, event, 1))">
						              		<f:converter converterId="convertNota"/>	
						              		<f:attribute name="linha" value="#{r}" />
						              	 	<f:attribute name="coluna" value="#{c}"/>
										</h:inputText>
									</c:if>
									<c:if test="${lancamentoFrequenciaIMD.dataAtual < colunas.dataInicio}">
			              				<h:inputText value="#{tab[c].frequencia}" size="1" maxlength="3" tabindex="#{c}" onblur="validaValorFrequencia(this);" onkeydown="return(formataValorNota(this, event, 1))" disabled="true">
						              		<f:converter converterId="convertNota"/>	
						              		<f:attribute name="linha" value="#{r}" />
						              	 	<f:attribute name="coluna" value="#{c}"/>
										</h:inputText>
									</c:if>
									
						        </rich:columns>
							</rich:dataTable>
						</td>
					</tr>
				</tbody>
				<tfoot>   
					<tr>
						<td>
							<h:commandButton value="Salvar as Frequ�ncias" action="#{lancamentoFrequenciaIMD.salvar}"/>
							<h:commandButton value="Cancelar" action="#{lancamentoFrequenciaIMD.voltarTelaPortal}" onclick="#{confirm}"/>
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>
		<c:if test="${(empty lancamentoFrequenciaIMD.listaDiscentesTurma) || (empty lancamentoFrequenciaIMD.listaPeriodosTurma) || (empty lancamentoFrequenciaIMD.tabelaAcompanhamento) }">
			<center><i> Nenhum discente e/ou per�odo encontrado.</i></center>
		</c:if>
		
	</h:form>
 	
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>