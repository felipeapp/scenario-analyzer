<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
	<style>
		#textoConteudo {
			line-height: 150%;
			font-family: serif;
			font-size: 12pt;
		}
	</style>    
	<c:if test="${requestScope.liberaEmissao == true}"> <%-- segurança importante senao o usuario pode acessar a pagina diretamente e ver o documento --%>
		<div id="textoConteudo">	
			<h2><u>ANEXO I</u></h2>
			<h2>		
				TERMO DE COMPROMISSO DO ESTAGIÁRIO PARA A REALIZAÇÃO DE ESTÁGIO CURRICULAR SUPERVISIONADO
			</h2>	
			<h3>${estagioMBean.obj.tipoEstagio.descricao}</h3>
			<p style="margin-left: 0.5cm; margin-bottom: 0cm">&nbsp;</p>
			<p style="margin-bottom: 0cm">
			   (Instrumento decorrente do Convênio n&ordm; 
			   ${estagioMBean.obj.concedente.convenioEstagio.numeroConvenio} / ${ configSistema['siglaInstituicao'] })
			</p>
			<p style="margin-left: 0.5cm; margin-bottom: 0cm">&nbsp;</p>
			<p style="text-align: justify; text-indent: 25px;">
				Pelo presente Instrumento, o(a) estudante ${estagioMBean.obj.discente.nome}, 
				do ${estagioMBean.obj.discente.periodoAtual}&deg; Período do Curso de ${estagioMBean.obj.discente.curso.nome}, 
				matrícula n&deg; ${estagioMBean.obj.discente.matricula}, RG n&deg; ${estagioMBean.obj.discente.pessoa.identidade}, 
				CPF n&deg; ${estagioMBean.obj.discente.pessoa.cpfCnpjFormatado}, regularmente matriculado e com efetiva frequência 
				doravante denominado ESTAGIÁRIO e ${estagioMBean.obj.concedente.pessoa.nome}, 
				doravante denominado CONCEDENTE, representado(a) por seu ${estagioMBean.obj.concedente.responsavel.cargo},
				o(a) Sr(a). ${estagioMBean.obj.concedente.responsavel.pessoa.nome}, portador do Registro Geral n&deg; 
				${estagioMBean.obj.concedente.responsavel.pessoa.identidade.numero}, 
				e do CPF n&deg; ${estagioMBean.obj.concedente.responsavel.pessoa.cpfCnpjFormatado}, 
				com a interveniência obrigatória da ${ configSistema['nomeInstituicao'] }, doravante denominada 
				${ configSistema['siglaInstituicao'] }, neste ato representada pelo Coordenador do Curso de 
				${estagioMBean.obj.discente.curso.descricao}, Prof.(a) ${estagioMBean.coordenador.pessoa.nome}, 
				RG n&deg; ${estagioMBean.coordenador.pessoa.identidade}, CPF n&deg; ${estagioMBean.coordenador.pessoa.cpfCnpjFormatado}, 
				e em conformidade com o que determina a Lei n&ordm; 11.788, de 25 de 
				setembro de 2008, a Resolução n&ordm; 178-CONSEPE, de 22 de setembro de 1992, 
				a Resolução n&ordm; 227/2009 - CONSEPE, de 03 de dezembro de 2009,
				resolvem firmar o presente Termo, mediante as seguintes cláusulas e condições:
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CLÁUSULA PRIMEIRA -</b> 			
				O Estágio possibilitará ao ESTAGIÁRIO o desenvolvimento de atividades práticas relacionadas à 
				sua área de formação acadêmica, constituindo-se componente indispensável para a integralização curricular.			
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CLÁUSULA SEGUNDA -</b> O Estágio se realizará no(a) 
				${estagioMBean.obj.concedente.pessoa.nome}, situado na ${estagioMBean.obj.concedente.pessoa.enderecoContato.descricao}, 
				${estagioMBean.obj.concedente.pessoa.enderecoContato.municipio.nomeUF}, no período de 
				<ufrn:format type="data" valor="${estagioMBean.obj.dataInicio}"/> a 
				<ufrn:format type="data" valor="${estagioMBean.obj.dataFim}"/> 
				correspondendo ao cumprimento da carga horária, no total de ${estagioMBean.duracaoEstagio}
				horas/aula.
			</p>
			<br/>
			<p style="text-align: justify; text-indent: 25px; margin-bottom: 0cm">
				<b>SUBCLÁUSULA PRIMEIRA -</b> Na modalidade de Estágio Curricular Obrigatório,
				o total de horas será estabelecido pela disciplina de estágio.
			</p>
			<br/>
			<p style="text-align: justify; text-indent: 25px; margin-bottom: 0cm">
				<b>SUBCLÁUSULA SEGUNDA -</b> Na modalidade de Estágio Curricular Não Obrigatório,
				o estágio terá período mínimo de 06 (seis) meses e máximo de 02 (dois) anos.
			</p>
			<br/>				
			<p style="text-align: justify;">
				<b>CLÁUSULA TERCEIRA -</b> A jornada de atividade do ESTAGIÁRIO será 
				de até ${estagioMBean.obj.cargaHorariaSemanal / 5} horas diárias e 
				até ${estagioMBean.obj.cargaHorariaSemanal} horas semanais, 
				a ser cumprida de segunda a sexta-feira, das <ufrn:format type="hora" valor="${estagioMBean.obj.horaInicio}"/>
				 às <ufrn:format type="hora" valor="${estagioMBean.obj.horaFim}"/> horas, sendo vedado o regime de hora extraordinária, 
				bem como a realização do estágio aos domingos e feriados.
			</p>
			<br/>
			<p style="text-align: justify; text-indent: 25px; margin-bottom: 0cm">
				<b>SUBCLÁUSULA PRIMEIRA -</b> Em nenhuma hipótese o estágio poderá 
				ser realizado concomitantemente com o horário escolar, não podendo coincidir com 
				este no todo ou em parte.
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CLÁUSULA QUARTA - </b>Durante o estágio, O ESTAGIÁRIO realizará as
				atividades previamente planejadas de acordo com o Plano de Atividades, 
				constante na CLÁUSULA DÉCIMA deste termo,
				 sob a orientação do Professor ${estagioMBean.obj.orientador.pessoa.nome}, da ${ configSistema['siglaInstituicao'] }
				 e sob a supervisão do(a) Sr(a). ${estagioMBean.obj.supervisor.nome}, da Concedente.
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CLÁUSULA QUINTA - </b>
				 Durante a realização do Estágio, o ESTAGIÁRIO estará amparado contra acidentes pessoais, 
				 através da Apólice de Seguro n&ordm; ${estagioMBean.obj.apoliceSeguro} da ${estagioMBean.obj.seguradora}, 
				 CNPJ / MF n&ordm; ${estagioMBean.obj.cnpjSeguradoraFormatado}, no valor de 
				 <ufrn:format type="valor" valor="${estagioMBean.obj.valorSeguro}"/> 
				(<ufrn:format type="extenso" valor="${estagioMBean.obj.valorSeguro}"/>), 
				 sob a responsabilidade da ${ configSistema['siglaInstituicao'] }, quando 
				 se tratar de Estágio Curricular Obrigatório e responsabilidade da CONCEDENTE,
				 quando se tratar de Estágio Curricular Não Obrigatório.
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CLÁUSULA SEXTA - </b>A realização do estágio não acarretará
				 por parte do estudante, vínculo empregatício de qualquer natureza, desde que respeitado o 
				&sect;2&ordm; do Art. 3&ordm; da Lei 11.788/08.
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CLÁUSULA SÉTIMA - </b>O ESTAGIÁRIO se compromete a:
			</p>
			<ol type="a">
			    <li>
			    	<p>Realizar, com responsabilidade e esmero, as atividades que lhe forem atribuídas;</p>
			    </li>
			    <li>
			    	<p>Zelar pelos 	materiais, equipamentos e bens em geral do(a) CONCEDENTE, que 	estejam sob os seus cuidados;</p>
			    </li>
			    <li>
			    	<p>Conhecer e cumprir 	os regulamentos e normas internas do Concedente, especialmente 	aquelas 
				que resguardem a manutenção do sigilo das informações a 	
				que tiver acesso em decorrência do estágio;
				</p>
			    </li>
			    <li>
			    	<p>Apresentar ao Concedente e à ${ configSistema['siglaInstituicao'] } relatórios semestrais sobre o 
				desenvolvimento	das atividades realizadas;
				</p>
			    </li>
			    <li>
			    	<p>Manter conduta disciplinar de acordo com a moral e os bons costumes;</p>
			    </li>
			    <li>
			    	<p>Comunicar ao Concedente e à ${ configSistema['siglaInstituicao'] }, quando houver conclusão 
				ou interrupção do 	curso;
				</p>
			    </li>
			    <li>
			   	<p>Responder pelas 	perdas e danos consequentes da inobservância das normas e
				condições estabelecidas neste Termo.
				</p>
			    </li>
			</ol>
			<br/>
			<p>
				<b>CLÁUSULA OITAVA - </b>O ESTAGIÁRIO será desligado do estágio:
			</p>
			<ol type="a">
			    <li>
			    	<p>Automaticamente, quando do término do Estágio;</p>
			    </li>
			    <li>
			    	<p>A qualquer tempo, no interesse ou conveniência do CONCEDENTE e/ou da ${ configSistema['siglaInstituicao'] };</p>
			    </li>
			    <li>
			    	<p>A seu pedido;</p>
			    </li>
			    <li>
			    	<p>Por descumprimento de cláusula do Termo de Compromisso;</p>
			    </li>
			    <li>
			   	<p>Quando houver conclusão ou interrupção do curso.</p>
			    </li>
			    <li>
			   	<p>Depois de decorrida a terça parte do tempo previsto para a duração
			   	do estágio, se comprovada a insuficiência na avaliação de desempenho
			   	no órgão ou entidade ou na instituição de ensino.</p>
			    </li>		    
			    <li>
			     <p>Pelo não comparecimento, sem motivo justificado, por mais de cinco
			     dias, consecutivos ou não, no período de um mês, ou por trinta dias
			     durante todo o período do estágio.</p>
			    </li>
			</ol>
			<br/>
			<p>
				<b>CLÁUSULA NONA - </b>Da Bolsa e Auxílio Transporte
			</p>		
			<br/>
			<p style="text-align: justify;">
				Quando se tratar de Estágio curricular Não Obrigatório ou Estágio Curricular
				Obrigatório Remunerado, o ESTAGIÁRIO receberá bolsa mensal no valor de 						 
				R$ <ufrn:format type="valor" valor="${estagioMBean.obj.valorBolsa}"/> 
				(<ufrn:format type="extenso" valor="${estagioMBean.obj.valorBolsa}"/>), 
				e Auxílio Transporte diário, no valor de  
				R$ <ufrn:format type="valor" valor="${estagioMBean.obj.valorAuxTransporte}"/> 
				(<ufrn:format type="extenso" valor="${estagioMBean.obj.valorAuxTransporte}"/>)  ao dia, 
				devendo respeitar o disposto na Orientação Normativa n&ordm; 07, de 30 de outubro de 2008,
				do Ministério de Estado do Planejamento, Orçamento e Gestão, quando o CONCEDENTE
				for Órgão Federal.
			</p>
			<br/>
			<p>
				<b>CLÁUSULA DÉCIMA - </b>O ESTAGIÁRIO realizará as seguintes atividades:
			</p>
			<br/>
			<p style="text-align: justify; text-indent: 25px;">
				${estagioMBean.obj.descricaoAtividades}
			</p>
			<br/>						
			<p>E por estarem assim justos e acordados, firmam o presente Termo de Compromisso.</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				${ configSistema['cidadeInstituicao'] }, <ufrn:format type="dia_mes_ano" valor="${estagioMBean.obj.dataAprovacao != null ? estagioMBean.obj.dataAprovacao : estagioMBean.obj.dataCadastro}"  />.
			</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				__________________________________<br/>
				${estagioMBean.obj.discente.nome}<br/>
				ESTAGIÁRIO			
			</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				__________________________________<br/>
				${estagioMBean.obj.concedente.pessoa.nome}<br/>
				CONCEDENTE			
			</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				__________________________________<br/>
				${estagioMBean.obj.supervisor.nome}<br/>
				SUPERVISOR DE CAMPO			
			</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				__________________________________<br/>
				${estagioMBean.coordenador.pessoa.nome}<br/>
				COORDENADOR DO CURSO			
			</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				__________________________________<br/>
				${estagioMBean.obj.orientador.pessoa.nome}<br/>
				ORIENTADOR			
			</p>
			<br/><br/>
		</div>
		<style>
			#divAutenticacao {
				width: 97%;
				margin: 10px auto 2px;
				text-align: center;
			}
			
			#divAutenticacao h4 {
				border-bottom: 1px solid #BBB;
				margin-bottom: 3px;
				padding-bottom: 2px;
			}
			
			#divAutenticacao span {
				color: #922;
				font-weight: bold;
			}	
		</style>
		
		<div id="divAutenticacao">
			<h4>ATENÇÃO</h4>
			<p>
				Para verificar a autenticidade deste documento acesse
				<span>${ configSistema['linkSigaa'] }/sigaa/documentos/</span> informando o identificador <i>(${estagioMBean.identificadorDocumento})</i>, a data de emissão e
				o código de verificação <span>${estagioMBean.codigoSeguranca}</span>
			</p>
		</div>				
	</c:if>
	<c:if test="${!requestScope.liberaEmissao}"> <%-- segurança importante senao o usuario pode acessar a pagina diretamente e ver o documento --%>
			<style type="text/css">
				
				#div1{
					font-weight: bold;
					color: red;
					width: 100%;
					text-align: center;
				}
				
				#div2{
					margin-top:  20px;  
					font-style: italic;
					width: 100%;
					text-align: center;
				}
			
			</style>			
				
			<div style="margin-bottom:30px;">	
				<div id="div1"> ERRO NA GERAÇÃO DO TERMO DE RESCISÃO DE ESTÁGIO CURRICULAR SUPERVISIONADO.</div>
		
				<div id="div2"> As informações do documento são inválidas </div>
			</div>	
	</c:if>		

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>	