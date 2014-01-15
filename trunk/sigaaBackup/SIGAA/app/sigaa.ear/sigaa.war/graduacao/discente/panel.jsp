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


<%-- F�RMULAS_INDICES_ACAD�MICO --%>
<rich:modalPanel id="panel" autosized="true" minWidth="700">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="C�LCULOS DOS INDICADORES DE RENDIMENTO ACAD�MICO ACUMULADO"></h:outputText>
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
					<p align="center"><b>M�dia de Conclus�o (MC)</b></p><br>
					
					<p>
					A <b>M�dia de Conclus�o (MC)</b> � a m�dia ponderada do rendimento escolar final nos
					componentes curriculares em que conseguiu �xito ao longo do curso, obtida pela seguinte f�rmula:
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
					Nessa f�rmula, s�o contabilizados todos os <b>Nx</b> componentes curriculares conclu�dos com
					�xito, incluindo os aproveitamentos, onde <b>ni</b> � a nota (rendimento escolar) final obtida no i-�simo 
					componente curricular e ci � a carga hor�ria discente do <b>i-</b>�simo componente curricular. S�o
					exclu�dos do c�lculo os componentes curriculares trancados, cancelados, reprovados e dispensados,
					as atividades complementares e os componentes curriculares cujo rendimento escolar n�o �
					expresso de forma num�rica.			
					</p>
				</td>
			</tr>
		</table>
		<br><br>
		<table>	
			<tr>
				<td>
					<p align="center"><b> M�dia de Conclus�o Normalizada (MCN)</b></p><br>
					<p>
					A <b> M�dia de Conclus�o Normalizada (MCN)</b> � a MC do aluno normalizada em rela��o �
					m�dia (&mu;) e desvio padr�o amostral (&sigma;) das MCs dos concluintes da mesma modalidade do curso,
					obtida pela seguinte f�rmula:
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
					Nessa f�rmula, <b>MC</b> � a M�dia de Conclus�o do aluno para o qual est� sendo calculada a <b>MCN</b>. 
					A m�dia (&mu;) e desvio padr�o amostral (&sigma;) s�o calculados pela seguinte f�rmula:
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
					Nessas f�rmulas, s�o contabilizados todos os <b>M</b> alunos que conclu�ram o mesmo
					curso/modalidade nos �ltimos 05 (cinco) anos, onde <b>MCi</b> � a M�dia de Conclus�o final obtida pelo
					<b>i-</b>�simo concluinte. S�o exclu�dos do c�lculo os alunos que n�o conclu�ram com �xito o curso por
					qualquer motivo bem como aqueles que fizeram apenas apostilamento de habilita��o ou certifica��o
					de �nfase.
					</p>
				</td>
			</tr>
		</table>
		<br><br>
		<table>
			<tr>
				<td>
					<p align="center"><b>�ndice de Rendimento Acad�mico (IRA)</b></p><br>
					<p>
					O <b>�ndice de Rendimento Acad�mico (IRA)</b> � a m�dia ponderada do rendimento escolar
					final obtido pelo aluno em todos os componentes curriculares que concluiu ao longo do curso,
					obtida pela seguinte f�rmula:
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
					Nessa f�rmula, s�o contabilizados todos os <b>N</b>
					componentes curriculares conclu�dos, seja
					com aprova��o ou com reprova��o por nota ou frequ�ncia, onde <b>ni</b> � a nota (rendimento escolar)
					final obtida no <b>i-</b>�simo componente curricular e <b>ci</b> � a carga hor�ria discente do <b>i-</b>�simo componente
					curricular. S�o exclu�dos do c�lculo os componentes curriculares trancados, cancelados e
					dispensados, as atividades complementares e os componentes curriculares cujo rendimento escolar
					n�o � expresso de forma num�rica.	
					</p>
				</td>
			</tr>
		</table>
		<br><br>
		<table>
			<tr>
				<td>
					<p align="center"><b>�ndice de Efici�ncia em Carga Hor�ria (IECH)</b></p><br>
					<p>
					O <b>�ndice de Efici�ncia em Carga Hor�ria (IECH)</b> � o percentual da carga hor�ria
					utilizada pelo aluno que se converteu em aprova��o, obtido pela seguinte f�rmula:
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
					Nessa f�rmula, s�o contabilizados no numerador todos os <b>Np</b> componentes curriculares em
					que o aluno obteve aprova��o, excluindo-se os componentes curriculares trancados, cancelados,
					reprovados, aproveitados e dispensados, as atividades complementares, as atividades individuais e
					as atividades de orienta��o individual. S�o contabilizados no denominador todos os <b>Nm</b>
					componentes curriculares em que o aluno se matriculou, incluindo os trancamentos, reprova��es e
					cancelamentos de matr�cula e excluindo-se os componentes curriculares aproveitados e
					dispensados, as atividades complementares, as atividades individuais e as atividades de orienta��o
					individual. <b>ci</b> � a carga hor�ria discente do <b>i-</b>�simo componente curricular.
					</p>
				</td>
			</tr>
		</table>
		<br><br>
		<table>
			<tr>
				<td>
					<p align="center"><b>�ndice de Efici�ncia em Per�odos Letivos (IEPL)</b></p><br>
					<p>
					O <b>�ndice de Efici�ncia em Per�odos Letivos (IEPL)</b> � divis�o da carga hor�ria acumulada
					pela carga hor�ria esperada, obtida pela seguinte f�rmula:	
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
					Nessa f�rmula, s�o contabilizados todos os <b>Na</b> componentes curriculares em que o aluno
					acumulou carga hor�ria depois que ingressou no curso de gradua��o, excluindo-se os componentes
					curriculares aproveitados. <b>ci</b> � a carga hor�ria discente do <b>i-</b>�simo componente curricular. P � o
					n�mero de per�odos j� cursados pelo aluno. CHM e PM s�o a carga hor�ria m�nima e o prazo
					m�dio, respectivamente, para integraliza��o da estrutura curricular do aluno.
					</p>
				</td>
			</tr>
		</table>
		<br><br>
		<table>
			<tr>
				<td>
					<p align="center"><b>�ndice de Efici�ncia Acad�mica (IEA)</b></p><br>
					<p>
					O <b>�ndice de Efici�ncia Acad�mica (IEA)</b> � o produto da MC pelo IECH e pelo IEPL,
					conforme a seguinte f�rmula:
					</p>
					<br>
					<p>
					<b>IEA = MC � IECH � IEPL</b>
					</p>
				</td>
			</tr>
		</table>
			<br><br>
		<table>
			<tr>
				<td>
					<p align="center"><b>�ndice de Efici�ncia Acad�mica Normalizado (IEAN)</b></p><br>
					<p>
					O <b>�ndice de Efici�ncia Acad�mica Normalizado (IEAN)</b> � o produto da MCN pelo IECH
					e pelo IEPL, conforme a seguinte f�rmula:
					</p>
					<br>
					<p>
					<b>IEAN = MCN � IECH � IEPL</b>
					</p>
				</td>
			</tr>
		</table>
	</div>
	
	<br/>
	
</rich:modalPanel>

