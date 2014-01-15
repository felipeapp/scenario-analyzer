<style>

	.listagemPopup {
		width: 100%;
		margin: 0 auto;
		text-align: left;
		font-size: 12px;
		padding-left: 10px;
	}
	
	.colunas td {
		font-weight: bold;
		border-bottom: 1px solid;
	}
	
</style>


<%-- FÓRMULAS_INDICES_ACADÊMICO --%>
<rich:modalPanel id="panel" autosized="true" minWidth="700">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="CÁLCULOS DOS INDICADORES DE RENDIMENTO ACADÊMICO ACUMULADO"></h:outputText>
		</h:panelGroup>
	</f:facet>
	<f:facet name="controls">
		<h:panelGroup>
			<h:graphicImage value="/img/close.png" styleClass="hidelink"
				id="hidelink" />
			<rich:componentControl for="panel" attachTo="hidelink" operation="hide" event="onclick" />
		</h:panelGroup>
	</f:facet>

	<div style="height: 250px; overflow: auto;">
		<table>
			<tr>
				<td>
					<p align="center"><b>Média de Conclusão (MC)</b></p><br>
					
					<p>
					A <b>Média de Conclusão (MC)</b> é a média ponderada do rendimento escolar final nos
					componentes curriculares em que conseguiu êxito ao longo do curso, obtida pela seguinte fórmula:
					</p>
				</td>
			</tr>
			<tr>
				<td>
					<h:graphicImage value="/img/formulas_indices/formulaMC.png" styleClass="hidelink" id="imgMC" />
				</td>
			</tr>
			<tr>
				<td>
					<p>
					Nessa fórmula, são contabilizados todos os <b>Nx</b> componentes curriculares concluídos com
					êxito, incluindo os aproveitamentos, onde <b>ni</b> é a nota (rendimento escolar) final obtida no i-ésimo 
					componente curricular e ci é a carga horária discente do <b>i-</b>ésimo componente curricular. São
					excluídos do cálculo os componentes curriculares trancados, cancelados, reprovados e dispensados,
					as atividades complementares e os componentes curriculares cujo rendimento escolar não é
					expresso de forma numérica.			
					</p>
				</td>
			</tr>
		</table>
		<br><br>
		<table>	
			<tr>
				<td>
					<p align="center"><b> Média de Conclusão Normalizada (MCN)</b></p><br>
					<p>
					A <b> Média de Conclusão Normalizada (MCN)</b> é a MC do aluno normalizada em relação à
					média (&mu;) e desvio padrão amostral (&sigma;) das MCs dos concluintes da mesma modalidade do curso,
					obtida pela seguinte fórmula:
					</p>
				</td>
			</tr>
			<tr>
				<td>
					<h:graphicImage value="/img/formulas_indices/formulaMCN1.png" styleClass="hidelink" id="imgMCN1" />
				</td>
			</tr>
			<tr>
				<td>
					<p>
					Nessa fórmula, <b>MC</b> é a Média de Conclusão do aluno para o qual está sendo calculada a <b>MCN</b>. 
					A média (&mu;) e desvio padrão amostral (&sigma;) são calculados pela seguinte fórmula:
					</p>
				</td>
			</tr>
			<tr>
				<td>
					<h:graphicImage value="/img/formulas_indices/formulaMCN2.png" styleClass="hidelink" id="imgMCN2" />
				</td>
			</tr>
			<tr>
				<td>
					<p>
					Nessas fórmulas, são contabilizados todos os <b>M</b> alunos que concluíram o mesmo
					curso/modalidade nos últimos 05 (cinco) anos, onde <b>MCi</b> é a Média de Conclusão final obtida pelo
					<b>i-</b>ésimo concluinte. São excluídos do cálculo os alunos que não concluíram com êxito o curso por
					qualquer motivo bem como aqueles que fizeram apenas apostilamento de habilitação ou certificação
					de ênfase.
					</p>
				</td>
			</tr>
		</table>
		<br><br>
		<table>
			<tr>
				<td>
					<p align="center"><b>Índice de Rendimento Acadêmico (IRA)</b></p><br>
					<p>
					O <b>Índice de Rendimento Acadêmico (IRA)</b> é a média ponderada do rendimento escolar
					final obtido pelo aluno em todos os componentes curriculares que concluiu ao longo do curso,
					obtida pela seguinte fórmula:
					</p>
				</td>
			</tr>
			<tr>
				<td>
					<h:graphicImage value="/img/formulas_indices/formulaIRA.png" styleClass="hidelink" id="imgIRA" />
				</td>
			</tr>
			<tr>
				<td>
					<p>
					Nessa fórmula, são contabilizados todos os <b>N</b>
					componentes curriculares concluídos, seja
					com aprovação ou com reprovação por nota ou frequência, onde <b>ni</b> é a nota (rendimento escolar)
					final obtida no <b>i-</b>ésimo componente curricular e <b>ci</b> é a carga horária discente do <b>i-</b>ésimo componente
					curricular. São excluídos do cálculo os componentes curriculares trancados, cancelados e
					dispensados, as atividades complementares e os componentes curriculares cujo rendimento escolar
					não é expresso de forma numérica.	
					</p>
				</td>
			</tr>
		</table>
		<br><br>
		<table>
			<tr>
				<td>
					<p align="center"><b>Índice de Eficiência em Carga Horária (IECH)</b></p><br>
					<p>
					O <b>Índice de Eficiência em Carga Horária (IECH)</b> é o percentual da carga horária
					utilizada pelo aluno que se converteu em aprovação, obtido pela seguinte fórmula:
					</p>
				</td>
			</tr>
			<tr>
				<td>
					<h:graphicImage value="/img/formulas_indices/formulaIECH.png" styleClass="hidelink" id="imgIECH" />
				</td>
			</tr>
			<tr>
				<td>
					<p>
					Nessa fórmula, são contabilizados no numerador todos os <b>Np</b> componentes curriculares em
					que o aluno obteve aprovação, excluindo-se os componentes curriculares trancados, cancelados,
					reprovados, aproveitados e dispensados, as atividades complementares, as atividades individuais e
					as atividades de orientação individual. São contabilizados no denominador todos os <b>Nm</b>
					componentes curriculares em que o aluno se matriculou, incluindo os trancamentos, reprovações e
					cancelamentos de matrícula e excluindo-se os componentes curriculares aproveitados e
					dispensados, as atividades complementares, as atividades individuais e as atividades de orientação
					individual. <b>ci</b> é a carga horária discente do <b>i-</b>ésimo componente curricular.
					</p>
				</td>
			</tr>
		</table>
		<br><br>
		<table>
			<tr>
				<td>
					<p align="center"><b>Índice de Eficiência em Períodos Letivos (IEPL)</b></p><br>
					<p>
					O <b>Índice de Eficiência em Períodos Letivos (IEPL)</b> é divisão da carga horária acumulada
					pela carga horária esperada, obtida pela seguinte fórmula:	
					</p>
				</td>
			</tr>
			<tr>
				<td>
					<h:graphicImage value="/img/formulas_indices/formulaIEPL.png" styleClass="hidelink" id="imgIEPL" />
				</td>
			</tr>
			<tr>
				<td>
					<p>
					Nessa fórmula, são contabilizados todos os <b>Na</b> componentes curriculares em que o aluno
					acumulou carga horária depois que ingressou no curso de graduação, excluindo-se os componentes
					curriculares aproveitados. <b>ci</b> é a carga horária discente do <b>i-</b>ésimo componente curricular. P é o
					número de períodos já cursados pelo aluno. CHM e PM são a carga horária mínima e o prazo
					médio, respectivamente, para integralização da estrutura curricular do aluno.
					</p>
				</td>
			</tr>
		</table>
		<br><br>
		<table>
			<tr>
				<td>
					<p align="center"><b>Índice de Eficiência Acadêmica (IEA)</b></p><br>
					<p>
					O <b>Índice de Eficiência Acadêmica (IEA)</b> é o produto da MC pelo IECH e pelo IEPL,
					conforme a seguinte fórmula:
					</p>
					<br>
					<p>
					<b>IEA = MC × IECH × IEPL</b>
					</p>
				</td>
			</tr>
		</table>
			<br><br>
		<table>
			<tr>
				<td>
					<p align="center"><b>Índice de Eficiência Acadêmica Normalizado (IEAN)</b></p><br>
					<p>
					O <b>Índice de Eficiência Acadêmica Normalizado (IEAN)</b> é o produto da MCN pelo IECH
					e pelo IEPL, conforme a seguinte fórmula:
					</p>
					<br>
					<p>
					<b>IEAN = MCN × IECH × IEPL</b>
					</p>
				</td>
			</tr>
		</table>
	</div>
	
	<br/>
	
</rich:modalPanel>

