<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<br/><br/>
<table class="listagem">
<thead>
<tr><th>DIMENSÃO 1</th><th>ITENS</th><th>DIM0001</th><th>DIM0002</th><th>DIM0003</th></tr>
</thead>
<tr><td rowspan="9"><strong>ATUAÇÃO<br/> DIDÁTICA E<br/> POSTURA<br/> PROFISSIONAL<br/> DO PROFESSOR<br/> QUANTO À(AO):</strong></td><td>1.1 - Comparecimento às aulas</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.2 - Cumprimento do horário das aulas do início ao fim</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.3 - Cumprimento do programa da disciplina</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.4 - Clareza na apresentação do conteúdo</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.5 - Utilização de metodologias que facilitem o aprendizado</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.6 - Incentivo à participação dos alunos nas aulas</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.7 - Disponibilidade para tirar dúvidas dos alunos durante as aulas</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.8 - Disponibilidade para atender aos alunos fora do horário das aulas</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.9 - Coerência entre o nível de exigência nas avaliações e o conteúdo dado</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
</table>

<br/>

<table class="listagem">
<thead>
<tr><th>DIMENSÃO 3</th><th>Assinale apenas uma alternativa que considere mais importante</th></tr>
</thead>
<tr><td rowspan="9"><strong>INFRA-ESTRUTURA<br/> DA ${ configSistema['siglaInstituicao'] }</strong></td><td><input type="radio"/> 3.1 - Ampliação do acervo da biblioteca</td></tr>
<tr><td><input type="radio"/> 3.2 - Ampliação do espaço físico da biblioteca</td></tr>
<tr><td><input type="radio"/> 3.3 - Construção de mais espaços de convivência</td></tr>
<tr><td><input type="radio"/> 3.4 - Melhoria das salas de aula</td></tr>
<tr><td><input type="radio"/> 3.5 - Melhoria dos laboratórios</td></tr>
<tr><td><input type="radio"/> 3.6 - Outra. Citar: <input type="text" size="60"/></td></tr>
</table>

<br/>

<table class="listagem">
<thead>
<tr><th>4 - ASSINALE "SIM" OU "NÃO" NAS QUESTÕES SEGUINTES</th><th></th></tr>
</thead>
<tr><td>4.1 - O professor disponibiliza o programa da disciplina na primeira semana de aulas?</td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>4.2 - O professor divulga as notas de uma avaliação antes da avaliação seguinte?</td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>4.3 - O professor discute com os alunos os resultados das avaliações, esclarecendo suas dúvidas?</td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>4.4 - O professor estimula a busca de fontes alternativas de informações?</td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>4.5 - Você tem orientador acadêmico?</td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>4.6 - Você teve orientação acadêmica neste semestre letivo?</td><td><%@ include file="combo.jsp" %></td></tr>
</table>

<br/>

<table class="listagem">
<tr><td rowspan="9"><strong>4.7.<br/> PARTICIPAÇÃO EM<br/> ATIVIDADES</td><td><strong>Assinale a(s) atividade(s) que você está participando neste período letivo</strong></td></tr>
<tr><td><input type="checkbox"/> 4.7.1 - Movimento estudantil</td></tr>
<tr><td><input type="checkbox"/> 4.7.2 - Projeto de Pesquisa</td></tr>
<tr><td><input type="checkbox"/> 4.7.3 - Projeto de Extensão</td></tr>
<tr><td><input type="checkbox"/> 4.7.4 - Iniciação à docência (monitoria)</td></tr>
<tr><td><input type="checkbox"/> 4.7.5 - Apresentação de trabalho em Seminário/Congresso</td></tr>
<tr><td><input type="checkbox"/> 4.7.6 - Outra. Citar: <input type="text" size="60"/></td></tr>
</table>

<br/>

<table class="listagem">
<tr><td rowspan="9"><strong>5.<br/> TRANCAMENTO DE<br/> DISCIPLINA(S)</td><td>5.1 - Realizou trancamento de disciplina(s) neste período letivo? </td><td>Sim</td></tr>
<tr><td>5.2 - Efetuou trancamento de quantas disciplinas? </td><td> 2</td></tr>
<tr><td colspan="2">5.3 - Relacione a(s) razão(ões) para o(s) trancamento(s)</td></tr>
<tr><td colspan="2"><table><tr><td>DIM0001</td><td><textarea cols="70"></textarea></td></tr></table> </td></tr>
<tr><td colspan="2"><table><tr><td>DIM0001</td><td><textarea cols="70"></textarea></td></tr></table> </td></tr>
</table>

<br/>

<table class="listagem">
<thead>
<tr><th style="text-align: center">Espaço destinado para comentários adicionais</th></tr></thead>
<tr><td><textarea cols="100" rows="10"></textarea></td></tr>
</table>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>